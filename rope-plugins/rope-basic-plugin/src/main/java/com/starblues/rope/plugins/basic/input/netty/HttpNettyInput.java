package com.starblues.rope.plugins.basic.input.netty;

import com.google.common.collect.Maps;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.Consumer;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.plugins.basic.input.netty.handler.StringMessageHandler;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * http netty 输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
@Component
public class HttpNettyInput extends AbstractTcpInput<String>{

    private static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
    private static final int DEFAULT_MAX_HEADER_SIZE = 8192;
    private static final int DEFAULT_MAX_CHUNK_SIZE = 65536;

    private final static String ID = "http";
    private final Config config;


    public HttpNettyInput(ConverterFactory converterFactory) {
        super(new Config(), converterFactory);
        config = (Config) super.tcpConfig;
    }

    @Override
    protected void toStart(Consumer consumer) throws Exception {
        super.toStart(consumer);
        log.info("Open http api[http://{}:{}{}][POST] successfully",
                config.getBindAddress(), config.getPort(), config.getPath());
    }

    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getBaseChannelHandlers(Consumer consumer) throws Exception {
        LinkedHashMap<String, Callable<? extends ChannelHandler>> handlerList =
                super.getBaseChannelHandlers(consumer);

        int idleWriteTimeout = config.getIdleWriteTimeout();
        if (idleWriteTimeout > 0) {
            handlerList.put("read-timeout-handler", () ->
                    new ReadTimeoutHandler(idleWriteTimeout, TimeUnit.SECONDS));
        }

        int tempMaxChunkSize = config.getMaxChunkSize();
        if(tempMaxChunkSize <= 0){
            tempMaxChunkSize = DEFAULT_MAX_CHUNK_SIZE;
        }
        final int maxChunkSize = tempMaxChunkSize;

        handlerList.put("decoder", () -> new HttpRequestDecoder(DEFAULT_MAX_INITIAL_LINE_LENGTH,
                DEFAULT_MAX_HEADER_SIZE, maxChunkSize));

        handlerList.put("aggregator", () -> new HttpObjectAggregator(config.getMaxChunkSize()));
        handlerList.put("encoder", HttpResponseEncoder::new);
        handlerList.put("decompressor", HttpContentDecompressor::new);
        return handlerList;
    }


    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getFinalChannelHandlers(Consumer consumer) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> handlers = Maps.newLinkedHashMap();

        handlers.put("http-handler", () -> new Handler(config.isEnableCros(), config.getPath()));
        handlers.put("message-handler", () -> new StringMessageHandler(message->{
            consumeMessage(message);
        }));


        handlers.putAll(super.getFinalChannelHandlers(consumer));

        return handlers;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "http-输入";
    }

    @Override
    public String describe() {
        return "http-输入";
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



    public static class Handler extends SimpleChannelInboundHandler<FullHttpRequest> {

        private final boolean enableCors;
        private final String path;

        public Handler(boolean enableCors, String path) {
            this.enableCors = enableCors;
            this.path = path;
        }


        @Override
        protected void channelRead0(ChannelHandlerContext ctx,
                                    FullHttpRequest request) throws Exception {
            if (is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        CONTINUE));
            }

            final boolean keepAlive = isKeepAlive(request);
            final HttpVersion httpRequestVersion = request.protocolVersion();
            final String origin = request.headers().get(ORIGIN);

            if (HttpMethod.OPTIONS.equals(request.method())) {
                writeResponse(ctx, keepAlive, httpRequestVersion, OK, origin);
                return;
            } else if (!HttpMethod.POST.equals(request.method())) {
                // 不是post请求，返回方法不允许
                writeResponse(ctx, keepAlive, httpRequestVersion, METHOD_NOT_ALLOWED, origin);
                return;
            }

            if(request.uri().equals(path)){
                writeResponse(ctx, keepAlive, httpRequestVersion, ACCEPTED, origin);
                ctx.fireChannelRead(request.content().toString(CharsetUtil.UTF_8));
            } else {
                writeResponse(ctx, keepAlive, httpRequestVersion, NOT_FOUND, origin);
            }
        }


        private void writeResponse(ChannelHandlerContext context,
                                   boolean keepAlive,
                                   HttpVersion httpRequestVersion,
                                   HttpResponseStatus status,
                                   String origin) {
            final FullHttpResponse response =
                    new DefaultFullHttpResponse(httpRequestVersion, status);

            response.headers().set(CONTENT_LENGTH, 0);
            response.headers().set(CONNECTION,
                    keepAlive ? "keep-alive" : "close");

            if (enableCors && origin != null && !origin.isEmpty()) {
                response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                response.headers().set(ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
                response.headers().set(ACCESS_CONTROL_ALLOW_HEADERS, "Authorization, Content-Type");
            }

            final ChannelFuture channelFuture = context.writeAndFlush(response);
            if (!keepAlive) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }


        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }


    @Getter
    @ToString
    private static class Config extends AbstractTcpInput.TcpConfig{

        private final static String ENABLE_CORS = "enable_cors";
        private final static String MAX_CHUNK_SIZE = "max_chunk_size";
        private final static String IDLE_WRITE_TIMEOUT = "idle_write_timeout";
        private final static String PATH = "path";

        private boolean enableCros;
        private int maxChunkSize;
        private int idleWriteTimeout;
        private String path;

        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            super.childParsing(paramInfo);
            enableCros = paramInfo.getBoolean(ENABLE_CORS, true);
            maxChunkSize = paramInfo.getInt(MAX_CHUNK_SIZE, DEFAULT_MAX_CHUNK_SIZE);
            idleWriteTimeout = paramInfo.getInt(IDLE_WRITE_TIMEOUT, 60);
            path = paramInfo.getString(PATH, "/");
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            super.configParam(configParam);

            configParam.addField(
                    BooleanField.toBuilder(ENABLE_CORS,
                            "CORS", true)
                            .required(true)
                            .description("是否启用跨域访问")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(MAX_CHUNK_SIZE, "最大请求大小", DEFAULT_MAX_CHUNK_SIZE)
                            .required(false)
                            .description("最大的HTTP请求大小")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(IDLE_WRITE_TIMEOUT, "空闲时写入超时", 60)
                            .required(false)
                            .description("服务器在最后一次客户端写入请求后的几秒钟内关闭连接。单位：秒")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );


            configParam.addField(
                    TextField.toBuilder(PATH, "路径", "/")
                            .required(false)
                            .description("http请求路径")
                            .build()
            );


        }
    }


}
