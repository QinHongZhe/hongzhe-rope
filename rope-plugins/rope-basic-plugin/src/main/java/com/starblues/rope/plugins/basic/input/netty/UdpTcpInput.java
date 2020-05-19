package com.starblues.rope.plugins.basic.input.netty;

import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.input.support.accept.AbstractAcceptConverterInput;
import com.starblues.rope.core.input.support.accept.BaseAcceptInputConfigParameter;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.plugins.basic.input.netty.confg.NettyInputConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * udp netty 输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
@Component
public class UdpTcpInput extends AbstractAcceptConverterInput<String> {

    private final static String ID = "udp";

    private final NettyInputConfig nettyInputConfig;

    private InetSocketAddress socketAddress;

    private NioEventLoopGroup eventLoopGroup;
    private ChannelFuture channelFuture;

    public UdpTcpInput(ConverterFactory converterFactory) {
        super(converterFactory);
        this.nettyInputConfig = new NettyInputConfig();
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "udp-输入";
    }

    @Override
    public String describe() {
        return "udp-输入";
    }


    @Override
    protected Class<String> sourceMessageType() {
        return String.class;
    }

    @Override
    protected Record customConvert(String sourceMessage) {
        Record record = DefaultRecord.instance();
        record.putColumn(Column.defaultAuto(sourceMessage));
        return record;
    }

    @Override
    public void initialize() throws Exception {
        this.socketAddress = new InetSocketAddress(
                nettyInputConfig.getBindAddress(),
                nettyInputConfig.getPort()
        );
    }

    @Override
    protected void toStart(Consumer consumer) throws Exception {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.group(eventLoopGroup);

        bootstrap.option(ChannelOption.SO_RCVBUF, nettyInputConfig.getReceiveBufferSize());

        bootstrap.handler(new ChannelInitializer<NioDatagramChannel>() {

            @Override
            protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
                ChannelPipeline cp = nioDatagramChannel.pipeline();
                cp.addLast("framer", new MessageToMessageDecoder<DatagramPacket>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx,
                                          DatagramPacket msg, List<Object> out) throws Exception {
                        out.add(msg.content().toString(CharsetUtil.UTF_8));
                    }
                }).addLast("handler", new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
                        String message = datagramPacket.content().toString(CharsetUtil.UTF_8);
                        consumeMessage(message);
                    }
                });
            }
        });

        channelFuture = bootstrap.bind(socketAddress);
        log.info("Bind [{}:{}] success", socketAddress.getHostString(), socketAddress.getPort());
    }


    @Override
    protected void toStop() throws Exception {
        if (channelFuture != null) {
            channelFuture.channel().closeFuture();
            channelFuture = null;
        }
        if(eventLoopGroup != null){
            eventLoopGroup.shutdownGracefully();
            eventLoopGroup = null;
        }
    }

    @Override
    public BaseAcceptInputConfigParameter configParameter() {
        return nettyInputConfig;
    }


}
