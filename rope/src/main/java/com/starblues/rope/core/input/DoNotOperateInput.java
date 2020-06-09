package com.starblues.rope.core.input;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.input.reader.Reader;
import com.starblues.rope.core.transport.Transport;

/**
 * 不可操作的输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DoNotOperateInput implements ReaderInput {

    private final Input input;

    public DoNotOperateInput(Input input) {
        this.input = input;
    }


    @Override
    public String processId() {
        return input.processId();
    }

    @Override
    public void initialize() throws Exception {
        throw new Exception("Do not operate initialize");
    }


    @Override
    public void start(Transport transport) throws Exception {
        throw new Exception("Do not operate start");
    }

    @Override
    public void stop() throws Exception {
        throw new Exception("Do not operate stop");
    }

    @Override
    public State state() {
        return input.state();
    }

    @Override
    public ConfigParameter configParameter() {
        return input.configParameter();
    }


    @Override
    public String id() {
        return input.id();
    }

    @Override
    public String name() {
        return input.name();
    }

    @Override
    public String describe() {
        return input.describe();
    }

    @Override
    public void setReader(Reader reader) {
        if(input instanceof ReaderInput){
            throw new RuntimeException("Do not operate setReader");
        }
    }

    @Override
    public Reader getReader() {
        if(input instanceof ReaderInput){
            return ((ReaderInput) input).getReader();
        }
        return null;
    }


}
