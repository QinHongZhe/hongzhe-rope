package com.starblues.rope.core.model.record;

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 记录组
 *
 * @author zhangzhuo
 * @version 1.0
 */
@EqualsAndHashCode
@ToString
public class RecordGroup {

    private long byteSize = 0L;
    private List<Record> records;

    /**
     * Record本身需要的内存
     */
    public RecordGroup(){
        this.records = Lists.newArrayList();
    }



    public void addRecord(Record record){
        if(record == null){
            return;
        }
        records.add(record);
        this.byteSize = this.byteSize + record.getByteSize();
    }


    public long getByteSize() {
        return byteSize;
    }

    public List<Record> getRecords() {
        return records;
    }

    public int size(){
        return records.size();
    }

}
