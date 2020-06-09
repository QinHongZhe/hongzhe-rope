package com.starblues.rope.core.input.reader;

import com.starblues.rope.core.input.reader.consumer.Consumer;

/**
 * 不可操作的Reader
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DoNotOperateReader implements Reader{

    private final Reader reader;

    public DoNotOperateReader(Reader reader) {
        this.reader = reader;
    }


    @Override
    public void initialize(String processId) throws Exception {
        throw new Exception("Do not operate initialize");
    }

    @Override
    public void reader(Consumer consumer) throws Exception {
        reader.reader(consumer);
    }


    @Override
    public void destroy() throws Exception {
        throw new Exception("Do not operate destroy");
    }

    @Override
    public BaseReaderConfigParameter configParameter() {
        return reader.configParameter();
    }

    @Override
    public String id() {
        return reader.id();
    }

    @Override
    public String name() {
        return reader.name();
    }

    @Override
    public String describe() {
        return reader.describe();
    }
}
