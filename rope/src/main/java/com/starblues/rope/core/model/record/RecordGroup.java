package com.starblues.rope.core.model.record;

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 记录分组
 *
 * @author zhangzhuo
 * @version 1.0
 */
@EqualsAndHashCode
@ToString
public class RecordGroup {

    /**
     * 该记录组的字节大小
     */
    private long byteSize = 0L;

    /**
     * 记录组
     */
    private List<Record> records;



    public RecordGroup(){
        this.records = Lists.newArrayList();
    }

    /**
     * 得到单记录的记录组
     * @param record 单记录
     * @return 记录组
     */
    public static RecordGroup singleRecord(Record record){
        RecordGroup recordGroup = new RecordGroup();
        recordGroup.addRecord(record);
        return recordGroup;
    }


    /**
     * 添加单条记录
     * @param record 记录
     */
    public void addRecord(Record record){
        if(record == null){
            return;
        }
        records.add(record);
        this.byteSize = this.byteSize + record.getByteSize();
    }


    /**
     * 添加多条记录
     * @param records 记录集合
     */
    public void addRecord(Collection<Record> records){
        if(records == null || records.isEmpty()){
            return;
        }
        for (Record record : records) {
            addRecord(record);
        }
    }

    /**
     * 添加记录组的多条记录
     * @param recordGroup 记录
     */
    public void addRecordGroup(RecordGroup recordGroup){
        if(recordGroup == null){
            return;
        }
        List<Record> records = recordGroup.getRecords();
        if(records == null || records.isEmpty()){
            return;
        }
        for (Record record : records) {
            this.addRecord(record);
        }
    }



    /**
     * 添加List<Map<String, Object> 格式的数据
     * @param listMap List<Map<String, Object>
     */
    public void addListMap(List<Map<String, Object>> listMap){
        if(listMap == null || listMap.isEmpty()){
            return;
        }
        for (Map<String, Object> map : listMap) {
            if(map == null || map.isEmpty()){
                continue;
            }
            Record record = DefaultRecord.instance();
            map.forEach((k,v)->{
                record.putColumn(Column.auto(k, v));
            });
            addRecord(record);
        }
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

    public boolean isEmpty(){
        return records.size() == 0;
    }

}
