package com.starblues.rope.plugins.basic.input.netty;

import com.google.common.collect.Maps;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.BooleanField;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.model.record.Column;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.plugins.basic.input.netty.handler.StringMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

/**
 * tcp netty 输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
@Component
public class TcpNettyInput extends AbstractTcpInput<String>{

    private final static String ID = "tcp";

    private final Config config;

    private ByteBuf[] delimiter;

    public TcpNettyInput(ConverterFactory converterFactory) {
        super(new Config(), converterFactory);
        config = (Config) super.tcpConfig;
    }


    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getFinalChannelHandlers(Consumer consumer) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> handlers = Maps.newLinkedHashMap();

        handlers.put("framer", ()-> new DelimiterBasedFrameDecoder(config.getMaxMessageSize(), delimiter));

        handlers.put("decoder", StringDecoder::new);

        handlers.put("encoder", StringEncoder::new);

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
    public void initialize() throws Exception {
        super.initialize();
        boolean nullDelimiter = config.isNullDelimiter();
        if(nullDelimiter){
            delimiter = Delimiters.nulDelimiter();
        } else {
            delimiter = Delimiters.lineDelimiter();
        }
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
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "tcp-输入";
    }

    @Override
    public String describe() {
        return "tcp-输入";
    }


    @Getter
    @ToString
    private static class Config extends AbstractTcpInput.TcpConfig{

        public static final String USE_NULL_DELIMITER = "useNullDelimiter";
        private static final String MAX_MESSAGE_SIZE = "maxMessageSize";

        private boolean nullDelimiter = false;
        private int maxMessageSize;


        @Override
        protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
            super.childParsing(paramInfo);
            nullDelimiter = paramInfo.getBoolean(USE_NULL_DELIMITER, false);
            maxMessageSize = paramInfo.getInt(MAX_MESSAGE_SIZE, 2 * 1024 * 1024);
        }

        @Override
        protected void configParam(ConfigParam configParam) {
            super.configParam(configParam);

            configParam.addField(
                    BooleanField.toBuilder(USE_NULL_DELIMITER,
                            "空字节分隔", false)
                            .required(false)
                            .description("是否使用空字节作为分隔符。如果为否, 则默认使用换行作为分隔符")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(MAX_MESSAGE_SIZE, "最大消息", 2 * 1024 * 1024)
                            .required(true)
                            .description("最大消息字节数")
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .build()
            );

        }
    }


}
