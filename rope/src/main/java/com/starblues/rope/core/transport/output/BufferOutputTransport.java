package com.starblues.rope.core.transport.output;

import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.core.transport.buffer.AbstractBufferTransport;
import com.starblues.rope.process.factory.ProcessFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * 输出缓冲Transport
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class BufferOutputTransport extends AbstractBufferTransport {

    public BufferOutputTransport(ProcessFactory processFactory, Param param) {
        super(processFactory, new DefaultOutputTransport(processFactory, false), param);
    }


    @Override
    protected Consumer<RecordWrapper> getConsumer(Transport transport) {
        return recordWrapper -> {
            metric(recordWrapper);
            transport.input(recordWrapper);
        };
    }

    @Override
    protected Logger logger() {
        return log;
    }

    @Override
    protected String metricGroup() {
        return MetricsConstant.GROUP_OUTPUT_TRANSPORT;
    }

}
