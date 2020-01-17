package com.starblues.rope.core.transport.output;

import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.output.Output;
import com.starblues.rope.core.transport.AbstractTransport;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.process.factory.ProcessFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.Objects;

/**
 * 默认的数据输出传输器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class DefaultOutputTransport extends AbstractTransport {

    public static final String ID = "default";

    private final ProcessFactory processFactory;
    private final boolean isMetric;

    public DefaultOutputTransport(ProcessFactory processFactory,
                                  Boolean isMetric) {
        this.processFactory = Objects.requireNonNull(processFactory);
        if(isMetric != null){
            this.isMetric = isMetric;
        } else {
            this.isMetric = true;
        }
    }

    public DefaultOutputTransport(ProcessFactory processFactory) {
        this(processFactory, true);
    }

    @Override
    public void input(RecordWrapper recordWrapper) {
        String processId = recordWrapper.getProcessId();
        ProcessManager processManager = processFactory.getProcessManager(processId);
        if(processManager == null){
            return;
        }
        if(isMetric){
            metric(recordWrapper);
        }
        Output output = processManager.getOutput();
        output.output(recordWrapper);
    }


    @Override
    protected String metricGroup() {
        return MetricsConstant.GROUP_OUTPUT_TRANSPORT;
    }

    @Override
    protected Logger logger() {
        return log;
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
        return "默认的输入数据传入器";
    }

    @Override
    protected void startAfter() throws Exception {
    }

    @Override
    protected void stopAfter() throws Exception {
    }
}
