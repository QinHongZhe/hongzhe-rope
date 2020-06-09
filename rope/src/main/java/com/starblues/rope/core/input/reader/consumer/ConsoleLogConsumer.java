package com.starblues.rope.core.input.reader.consumer;

import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 控制台打印的消费者.主要用于单元测试
 *
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-10
 */
@Slf4j
public class ConsoleLogConsumer extends DoNotStateConsumer{


    public ConsoleLogConsumer() {
        super(null);
    }

    @Override
    public void accept(Record record) {
        log.info("{}", record);
    }

    @Override
    public void accept(RecordGroup recordGroup) {
        if(recordGroup == null){
            return;
        }
        List<Record> records = recordGroup.getRecords();
        if(records == null){
            return;
        }
        for (Record record : records) {
            accept(record);
        }
    }

}
