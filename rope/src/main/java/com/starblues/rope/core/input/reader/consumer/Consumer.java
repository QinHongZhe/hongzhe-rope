package com.starblues.rope.core.input.reader.consumer;

import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;

/**
 * 数据消费者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Consumer {


    /**
     * 流程id
     * @return String
     */
    String processId();


    /**
     * 接受单条数据
     * @param record 数据bean
     */
    void accept(Record record);


    /**
     * 接受数据集合
     * @param recordGroup 记录组
     */
    void accept(RecordGroup recordGroup);


}
