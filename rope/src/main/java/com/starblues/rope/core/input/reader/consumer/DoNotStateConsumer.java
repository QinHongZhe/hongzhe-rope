package com.starblues.rope.core.input.reader.consumer;

import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;

/**
 * 不能操作的消费者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DoNotStateConsumer extends AbstractStateConsumer {

    private final AbstractStateConsumer abstractStateConsumer;

    public DoNotStateConsumer(AbstractStateConsumer abstractStateConsumer) {
        this.abstractStateConsumer = abstractStateConsumer;
    }

    @Override
    protected void startup() throws Exception {
        throw new Exception("Do not operate startup");
    }

    @Override
    protected void shutdown() throws Exception {
        throw new Exception("Do not operate shutdown");
    }

    @Override
    protected void acceptImpl(Record record) {
    }

    @Override
    protected void acceptImpl(RecordGroup recordGroup) {
    }

    @Override
    public void accept(Record record) {
        abstractStateConsumer.accept(record);
    }

    @Override
    public void accept(RecordGroup recordGroup) {
        abstractStateConsumer.accept(recordGroup);
    }

    @Override
    public String processId() {
        return abstractStateConsumer.processId();
    }

}
