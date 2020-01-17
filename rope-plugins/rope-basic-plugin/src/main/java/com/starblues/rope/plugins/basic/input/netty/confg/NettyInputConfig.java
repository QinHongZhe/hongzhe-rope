package com.starblues.rope.plugins.basic.input.netty.confg;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.input.support.accept.BaseAcceptInputConfigParameter;
import lombok.Getter;
import lombok.ToString;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Getter
@ToString
public class NettyInputConfig extends BaseAcceptInputConfigParameter {

    private final static String BIND_ADDRESS = "bind_address";
    private final static String PORT = "port";
    private final static String RECEIVE_BUFFER_SIZE = "receive_buffer_size";

    private final static String DEFAULT_BIND_ADDRESS = "0.0.0.0";
    private final static int DEFAULT_PORT = 5000;
    private final static int DEFAULT_RECEIVE_BUFFER_SIZE = 1024 * 1024;

    private String bindAddress;
    private int port;
    private int receiveBufferSize;


    @Override
    protected void childParsing(ConfigParamInfo paramInfo) throws Exception {
        bindAddress = paramInfo.getString(BIND_ADDRESS, DEFAULT_BIND_ADDRESS);
        port = paramInfo.getInt(PORT, DEFAULT_PORT);
        receiveBufferSize = paramInfo.getInt(RECEIVE_BUFFER_SIZE, DEFAULT_RECEIVE_BUFFER_SIZE);
    }

    @Override
    protected void configParam(ConfigParam configParam) {
        configParam.addField(
                TextField.toBuilder(BIND_ADDRESS, "侦听地址", DEFAULT_BIND_ADDRESS)
                        .required(true)
                        .description("侦听地址. 例如 0.0.0.0 或 127.0.0.1")
                        .build()
        );

        configParam.addField(
                NumberField.toBuilder(PORT, "端口", DEFAULT_PORT)
                        .required(true)
                        .attribute(NumberField.Attribute.ONLY_POSITIVE)
                        .description("侦听的端口")
                        .build()
        );

        configParam.addField(
                NumberField.toBuilder(RECEIVE_BUFFER_SIZE, "接收缓冲区的大小", DEFAULT_RECEIVE_BUFFER_SIZE)
                        .required(true)
                        .attribute(NumberField.Attribute.ONLY_POSITIVE)
                        .description("用于网络连接至该输入的，recvBufferSize的大小。单位: 字节")
                        .build()
        );

    }
}
