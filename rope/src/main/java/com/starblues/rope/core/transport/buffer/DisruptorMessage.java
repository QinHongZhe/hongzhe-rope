package com.starblues.rope.core.transport.buffer;

import lombok.Builder;
import lombok.Data;


/**
 * disruptor 填充的bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@Builder
public class DisruptorMessage<T> {

    private T message;

    public static <T>  void transform(DisruptorMessage<T> disruptorMessage1, DisruptorMessage<T> disruptorMessage2){
        disruptorMessage1.setMessage(disruptorMessage2.getMessage());
        disruptorMessage2.setMessage(null);
    }

}
