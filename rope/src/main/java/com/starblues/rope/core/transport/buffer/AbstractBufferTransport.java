package com.starblues.rope.core.transport.buffer;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.transport.AbstractTransport;
import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.process.factory.ProcessFactory;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 抽象的 buffer Transport
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractBufferTransport extends AbstractTransport {

    public static final String ID = "buffer";

    private final DisruptorBuffer<RecordWrapper> disruptorBuffer;
    private final IWorkHandler<RecordWrapper>[] workHandlers;


    public AbstractBufferTransport(ProcessFactory processFactory,
                                   Transport transport,
                                   Param param) {
        Objects.requireNonNull(processFactory, "processFactory can't be null");
        Objects.requireNonNull(transport, "transport can't be null");
        Objects.requireNonNull(param, "bufferTransport.param can't be null");

        this.disruptorBuffer = new DisruptorBuffer<RecordWrapper>(param.getBufferSize(),
                param.getWaitStrategy());

        Consumer<RecordWrapper> consumer = getConsumer(transport);
        if(consumer == null){
            throw new RuntimeException("getConsumer can't is null");
        }

        this.workHandlers = new IWorkHandler[param.getConsumePoolSize()];
        for (int i = 0; i < param.getConsumePoolSize(); i++) {
            workHandlers[i] = new DefaultWorkHandler<RecordWrapper>(consumer);
        }
    }


    @Override
    protected void startAfter() throws Exception {
        disruptorBuffer.start(workHandlers);
    }

    @Override
    protected void stopAfter() throws Exception {
        disruptorBuffer.stop();
    }

    @Override
    public void input(RecordWrapper recordWrapper) {
        DisruptorMessage<RecordWrapper> disruptorMessage = DisruptorMessage.<RecordWrapper>builder()
                .message(recordWrapper)
                .build();
        disruptorBuffer.process(disruptorMessage);
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return ID;
    }

    @Override
    public String describe() {
        return "缓冲的输入数据传输器";
    }

    protected Consumer<RecordWrapper> getConsumer(Transport transport){
        return recordWrapper -> {
            transport.input(recordWrapper);
        };
    }

    @Getter
    public static class Param implements ConfigParameter {

        private static final String BUFFER_SIZE = "bufferSize";
        private static final String WAIT_STRATEGY = "waitStrategy";
        private static final String CONSUME_POOL_SIZE = "consumePoolSize";

        /**
         * 输入的缓冲大小，必须为2的n次方
         */
        private Integer bufferSize;

        /**
         * 输入的缓冲消费者等待策略, 可选: sleeping, yielding, blocking, busy_spinning
         */
        private String waitStrategy;

        /**
         * 缓冲消费池的消费者数量
         */
        private Integer consumePoolSize;


        @Override
        public void parsing(ConfigParamInfo configParamInfo) throws Exception {
            bufferSize = configParamInfo.getInt(BUFFER_SIZE);
            if(bufferSize == null){
                throw new Exception("AbstractBufferTransport 的 " + BUFFER_SIZE + " 参数为空");
            }
            waitStrategy = configParamInfo.getString(WAIT_STRATEGY);
            if (StringUtils.isEmpty(waitStrategy)) {
                waitStrategy = "blocking";
            }
            consumePoolSize = configParamInfo.getInt(CONSUME_POOL_SIZE);
            if(consumePoolSize == null || consumePoolSize <= 0){
                consumePoolSize = 1;
            }
        }

        @Override
        public ConfigParam configParam() {
            return null;
        }
    }



}
