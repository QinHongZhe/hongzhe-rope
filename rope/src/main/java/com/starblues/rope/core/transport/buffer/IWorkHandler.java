package com.starblues.rope.core.transport.buffer;

import com.lmax.disruptor.WorkHandler;

/**
 * 自定义 disruptor WorkHandler
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface IWorkHandler<T> extends WorkHandler<DisruptorMessage<T>>{
}
