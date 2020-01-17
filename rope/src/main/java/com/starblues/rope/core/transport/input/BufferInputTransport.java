package com.starblues.rope.core.transport.input;

import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.transport.buffer.AbstractBufferTransport;
import com.starblues.rope.process.factory.ProcessFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 输入缓冲Transport
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class BufferInputTransport extends AbstractBufferTransport {


    public BufferInputTransport(ProcessFactory processFactory, Param param) {
        super(processFactory, new DefaultInputTransport(processFactory, false), param);
    }


    @Override
    public void input(RecordWrapper recordWrapper) {
        metric(recordWrapper);
        super.input(recordWrapper);
    }

    @Override
    protected String metricGroup() {
        return MetricsConstant.GROUP_INPUT_TRANSPORT;
    }

    @Override
    protected Logger logger() {
        return log;
    }
}
