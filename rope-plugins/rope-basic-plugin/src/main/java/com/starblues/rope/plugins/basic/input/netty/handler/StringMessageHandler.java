package com.starblues.rope.plugins.basic.input.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * String 消息处理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class StringMessageHandler extends SimpleChannelInboundHandler<String> {

    private final Consumer<String> consumer;

    public StringMessageHandler(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        consumer.accept(message);
    }


}
