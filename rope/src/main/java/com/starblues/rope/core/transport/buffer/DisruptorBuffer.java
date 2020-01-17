package com.starblues.rope.core.transport.buffer;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;



/**
 * Disruptor Disruptor基本方法
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DisruptorBuffer<T> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * disruptor
     */
    private Disruptor<DisruptorMessage<T>> disruptor;

    /**
     * disruptor ring buffer
     */
    protected RingBuffer<DisruptorMessage<T>> ringBuffer;

    /**
     * disruptor 配置的大小
     */
    private Integer disruptorBufferSize;

    /**
     * disruptor ring 默认大小
     */
    private final Integer DEFAULT_DISRUPTOR_BUFFER_SIZE = 65536;

    /**
     * disruptor 等待策略
     */
    private WaitStrategy waitStrategy;


    /**
     * 填充Event
     */
    private final EventTranslatorOneArg<DisruptorMessage<T>, DisruptorMessage<T>> TRANSLATOR =
            (DisruptorMessage<T> response, long sequence, DisruptorMessage<T> request) -> {
                DisruptorMessage.transform(response, request);
            };


    public DisruptorBuffer(Integer disruptorBufferSize,
                           String waitStrategyName) {
        if(disruptorBufferSize == null){
            this.disruptorBufferSize = DEFAULT_DISRUPTOR_BUFFER_SIZE;
        } else {
            this.disruptorBufferSize = disruptorBufferSize;
        }
        this.waitStrategy = getWaitStrategy(waitStrategyName);
        EventFactory<DisruptorMessage<T>> eventFactory = new BufferEventFactory();
        this.disruptor = new Disruptor<DisruptorMessage<T>>(
                eventFactory,
                this.disruptorBufferSize,
                getThreadFactory(),
                ProducerType.MULTI,
                this.waitStrategy
        );
    }


    /**
     * 生产一组消息
     * @param message disruptor 消息体
     */
    public void process(DisruptorMessage<T> message) {
        ringBuffer.publishEvent(TRANSLATOR, message);
    }

    /**
     * 生产多组消息
     * @param messages disruptor 消息体
     */
    public void process(DisruptorMessage<T>[] messages) {
        ringBuffer.publishEvents(TRANSLATOR, messages);
    }

    /**
     * 获取到剩余容量
     * @return 返回剩余容量
     */
    public long remainingCapacity(){
        return ringBuffer.remainingCapacity();
    }

    /**
     * 获取已经使用的容量
     * @return 返回已经使用的容量
     */
    public long getUsage() {
        if (ringBuffer == null) {
            return 0;
        }
        return (long) ringBuffer.getBufferSize() - ringBuffer.remainingCapacity();
    }

    /**
     * 获取ring buffer 总容量
     * @return long
     */
    public long getBufferSize() {
        return ringBuffer.getBufferSize();
    }

    /**
     * 启动disruptor
     * @param workHandlers 消费处理者
     * @throws Exception 启动一次
     */
    public void start(IWorkHandler<T>... workHandlers) throws Exception {
        if(workHandlers == null || workHandlers.length == 0){
            throw new Exception("DisruptorBuffer 消费者不能为空");
        }
        disruptor.handleEventsWithWorkerPool(workHandlers);
        this.ringBuffer = disruptor.start();
    }

    /**
     * 停止disruptor
     */
    public void stop() {
        this.disruptor.shutdown();
    }


    /**
     * 获得线程工厂
     * @return ThreadFactory
     */
    private ThreadFactory getThreadFactory(){
        return (Runnable r) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("DispatchBuffer thread");
            return thread;
        };
    }

    /**
     * disruptor 的 EventFactory
     */
    private class BufferEventFactory implements EventFactory<DisruptorMessage<T>>{
        @Override
        public DisruptorMessage<T> newInstance() {
            return DisruptorMessage.<T>builder().build();
        }
    }

    /**
     * 获取等待策略
     * @param waitStrategyName 等待策略名称
     * @return 等待策略
     */
    private WaitStrategy getWaitStrategy(String waitStrategyName) {
        if(Objects.equals(WaitStrategyEnum.SLEEPING.getName(), waitStrategyName)){
            return new SleepingWaitStrategy();
        } else if(Objects.equals(WaitStrategyEnum.YIELDING.getName(), waitStrategyName)){
            return new YieldingWaitStrategy();
        } else if(Objects.equals(WaitStrategyEnum.BLOCKING.getName(), waitStrategyName)){
            return new BlockingWaitStrategy();
        } else if(Objects.equals(WaitStrategyEnum.BUSY_SPINNING.getName(), waitStrategyName)){
            return new BusySpinWaitStrategy();
        } else {
            LOGGER.warn("Invalid setting for [{}]:"
                    + " Falling back to default: BlockingWaitStrategy.", waitStrategyName);
            return new BlockingWaitStrategy();
        }
    }

    /**
     * 等待策略名称枚举
     */
    public enum WaitStrategyEnum{

        // 性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景
        SLEEPING("sleeping"),

        // 可以被用在低延迟系统中的两个策略之一，这种策略在减低系统延迟的同时也会增加CPU运算量。
        // YieldingWaitStrategy策略会循环等待sequence增加到合适的值。
        // 循环中调用Thread.yield()允许其他准备好的线程执行。如果需要高性能而且事件消费者线程比逻辑内核少的时候，
        // 推荐使用YieldingWaitStrategy策略。例如：在开启超线程的时候。
        YIELDING("yielding"),

        // BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现
        BLOCKING("blocking"),

        // 性能最高的等待策略，同时也是对部署环境要求最高的策略。这个性能最好用在事件处理线程比物理内核数目还要小的时候。
        // 例如：在禁用超线程技术的时候。
        BUSY_SPINNING("busy_spinning");

        private String name;

        WaitStrategyEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
