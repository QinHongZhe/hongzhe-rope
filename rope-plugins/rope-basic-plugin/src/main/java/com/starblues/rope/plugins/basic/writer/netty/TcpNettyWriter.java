package com.starblues.rope.plugins.basic.writer.netty;

import com.google.gson.Gson;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.output.writer.AbstractConverterWriter;
import com.starblues.rope.core.output.writer.BaseWriterConfigParameter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Getter;
import lombok.ToString;
import io.netty.bootstrap.Bootstrap;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * tcp netty 写入者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class TcpNettyWriter extends AbstractConverterWriter<String> {


    private final Gson gson;
    private final Config config;


    protected BlockingQueue<String> queue;
    private EventLoopGroup workerGroup;
    private SendRunner sendRunner;

    public TcpNettyWriter(Gson gson) {
        this.gson = gson;
        this.config = new Config();
    }


    @Override
    protected String customConvert(Record record) throws Exception {
        return gson.toJson(record);
    }

    @Override
    public void initialize(String processId) throws Exception {
        queue = new LinkedBlockingQueue<>(config.getQueueSize());
        sendRunner = new SendRunner(queue);
        workerGroup = new NioEventLoopGroup();
        createBootstrap(workerGroup);
    }


    protected void createBootstrap(final EventLoopGroup workerGroup) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(config.getHostname(), config.getPort());
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getTimeout())
                .remoteAddress(inetSocketAddress)
                .handler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringEncoder());

                        channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                sendRunner.start(ctx.channel());
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                log.info("{}:{} channel disconnected.", config.getHostname(),
                                        config.getPort());
                                sendRunner.stop();
                                scheduleReconnect(ctx.channel().eventLoop());
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                log.error("{}:{} channel connect exception. {}", config.getHostname(),
                                        config.getPort(), cause.getMessage(), cause);
                            }
                        });
                    }
                });

    }

    protected void scheduleReconnect(final EventLoopGroup workerGroup) {
        workerGroup.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("{}:{} start reconnect.", config.getHostname(), config.getPort());
                createBootstrap(workerGroup);
            }
        }, config.getRetryTime(), TimeUnit.SECONDS);
    }

    @Override
    public void write(List<String> strings) throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public BaseWriterConfigParameter configParameter() {
        return config;
    }

    @Override
    public String id() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public String describe() {
        return null;
    }

    @Slf4j
    private static class SendRunner implements Runnable{

        private final ReentrantLock lock;
        private final Condition connectedCond;
        private final AtomicBoolean keepRunning = new AtomicBoolean(true);
        private Channel channel;
        private final BlockingQueue<String> queue;

        private final Thread sendThread;

        public SendRunner(BlockingQueue<String> queue) {
            this.queue = queue;
            this.lock = new ReentrantLock();
            this.connectedCond = lock.newCondition();
            this.sendThread = new Thread();
        }

        @Override
        public void run() {
            String message = null;

            while (keepRunning.get()) {
                lock.lock();
                try {
                    while (channel == null || !channel.isActive()) {
                        try {
                            connectedCond.await();
                        } catch (InterruptedException e) {
                            if (!keepRunning.get()) {
                                break;
                            }
                        }
                    }
                    try {
                        if (message == null) {
                            message = queue.poll(100, TimeUnit.MILLISECONDS);
                        }
                        if (message != null) {
                            log.debug("Send to remote server <{}>", message);
                        }
                        if (message != null && channel != null && channel.isActive()) {
                            channel.writeAndFlush(message);
                            message = null;
                        }
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        public void start(Channel channel) {
            lock.lock();
            try {
                this.channel = channel;
                this.connectedCond.signalAll();
            } finally {
                lock.unlock();
            }
            sendThread.start();
        }

        public void stop() {
            keepRunning.set(false);
            sendThread.interrupt();
        }

    }


    @Getter
    @ToString
    private static class Config extends BaseWriterConfigParameter{


        private final static String HOSTNAME = "hostname";
        private final static String PORT = "port";

        private final static String QUEUE_SIZE = "queue_size";
        private final static String RETRY_TIME = "retry_time";
        private final static String TIMEOUT = "timeout";

        private String hostname;
        private int port;
        private int queueSize;
        private int retryTime;
        private int timeout;

        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            hostname = paramInfo.getString(HOSTNAME, "");
            port = paramInfo.getInt(PORT, 8080);

            queueSize = paramInfo.getInt(QUEUE_SIZE, 512);
            retryTime = paramInfo.getInt(RETRY_TIME, 15);
            timeout  = paramInfo.getInt(TIMEOUT, 5000);
        }

        @Override
        protected void configParam(ConfigParam configParam) {

            configParam.addField(
                    TextField.toBuilder(HOSTNAME, "目标地址", "")
                            .required(true)
                            .description("远程主机的主机名或IP地址")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(PORT, "目标端口", 8080)
                            .required(false)
                            .description("远程主机的端口")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(QUEUE_SIZE, "队列大小", 512)
                            .required(false)
                            .description("输出队列大小。单位：个")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(RETRY_TIME, "重试间隔", 15)
                            .required(false)
                            .description("连接失败, 重试间隔，单位：秒（s）")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(TIMEOUT, "连接超时时间", 5000)
                            .required(false)
                            .description("连接超时时间设置，单位：毫秒（ms）")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );
        }
    }



}
