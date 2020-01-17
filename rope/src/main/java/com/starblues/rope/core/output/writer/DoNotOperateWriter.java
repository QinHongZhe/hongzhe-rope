package com.starblues.rope.core.output.writer;

import com.starblues.rope.core.converter.Converter;
import com.starblues.rope.core.model.record.Record;

import java.util.List;

/**
 * 不可操作的写入者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DoNotOperateWriter implements Writer{

    private final Writer writer;

    public DoNotOperateWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void initialize(String processId) throws Exception {
        throw new Exception("Do not operate initialize");
    }

    @Override
    public boolean support(Converter converter) {
        return writer.support(converter);
    }

    @Override
    public Object convert(Record record) throws Exception {
        return writer.convert(record);
    }

    @Override
    public Class convertDataClass() {
        return writer.convertDataClass();
    }


    @Override
    public void destroy() throws Exception {
        throw new Exception("Do not operate destroy");
    }

    @Override
    public BaseWriterConfigParameter configParameter() {
        return writer.configParameter();
    }


    @Override
    public void write(List list) throws Exception {
        writer.write(list);
    }

    @Override
    public String id() {
        return writer.id();
    }

    @Override
    public String name() {
        return writer.name();
    }

    @Override
    public String describe() {
        return writer.describe();
    }
}
