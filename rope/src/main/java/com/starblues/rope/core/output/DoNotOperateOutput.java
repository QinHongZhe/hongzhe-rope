package com.starblues.rope.core.output;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.model.RecordWrapper;

import java.util.List;

/**
 * 不可操作的输出
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DoNotOperateOutput implements Output{

    private final Output output;

    public DoNotOperateOutput(Output output) {
        this.output = output;
    }


    @Override
    public State state() {
        return output.state();
    }

    @Override
    public ConfigParameter configParameter() {
        return output.configParameter();
    }

    @Override
    public void output(RecordWrapper recordWrapper) {
        output.output(recordWrapper);
    }


    @Override
    public String processId() {
        return output.processId();
    }

    @Override
    public void initialize() throws Exception {
        throw new Exception("Do not operate initialize");
    }

    @Override
    public void addWriter(WriterWrapper writerWrapper) {
        throw new RuntimeException("Do not operate addWriter");
    }


    @Override
    public void addWriter(List<WriterWrapper> writerWrappers) {
        throw new RuntimeException("Do not operate addWriter");
    }


    @Override
    public List<WriterWrapper> getWriter() {
        return output.getWriter();
    }

    @Override
    public void start() throws Exception {
        throw new Exception("Do not operate start");
    }

    @Override
    public void stop() throws Exception {
        throw new Exception("Do not operate stop");
    }

    @Override
    public String id() {
        return output.id();
    }

    @Override
    public String name() {
        return output.name();
    }

    @Override
    public String describe() {
        return output.describe();
    }
}
