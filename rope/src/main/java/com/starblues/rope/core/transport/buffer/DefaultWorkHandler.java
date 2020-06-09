package com.starblues.rope.core.transport.buffer;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 默认的消费处理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultWorkHandler<T> implements IWorkHandler<T> {


    private final Consumer<T> consumer;

    public DefaultWorkHandler(Consumer<T> consumer) {
        this.consumer = Objects.requireNonNull(consumer);
    }


    @Override
    public void onEvent(DisruptorMessage<T> disruptorMessage) throws Exception {
        T message = disruptorMessage.getMessage();
        if(message == null){
            return;
        }
        disruptorMessage.setMessage(null);
        consumer.accept(message);
    }



}
