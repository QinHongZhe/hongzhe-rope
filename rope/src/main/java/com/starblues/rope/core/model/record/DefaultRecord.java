package com.starblues.rope.core.model.record;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.starblues.rope.utils.ClassSize;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 默认的记录
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultRecord implements Record{

    private static final int RECORD_COLUMN_NUMBER = 16;

    /**
     * 数据记录的列集合
     */
    private Map<String, Column> columns;

    /**
     * 数据的字节大小
     */
    private int byteSize;

    /**
     * Record本身需要的内存
     */
    private long oneselfMemorySize = ClassSize.DefaultRecordHead;

    public DefaultRecord() {
        this.columns = Maps.newHashMapWithExpectedSize(RECORD_COLUMN_NUMBER);
    }

    /**
     * 静态实例化
     * @return DefaultRecord
     */
    public static DefaultRecord instance(){
        return new DefaultRecord();
    }


    @Override
    public void putColumn(Column column) {
        if(column == null ){
            return;
        }
        String key = column.getKey();
        if(StringUtils.isEmpty(key)){
            throw new RuntimeException("add column key can't be empty in record");
        }
        Column oldColumn = columns.get(key);
        if(oldColumn != null){
            decrByteSize(oldColumn);
            columns.remove(key);
        }
        incrByteSize(column);
        columns.put(column.getKey(), column);
    }

    @Override
    public void removeColumn(String key) {
        Column column = columns.get(key);
        if(column == null){
            return;
        }
        decrByteSize(column);
        columns.remove(key);
    }

    @Override
    public Collection<Column> getColumns() {
        return Collections.unmodifiableCollection(columns.values());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        columns.forEach((k, column)->{
            if(StringUtils.isEmpty(k) || column == null){
                return;
            }
            map.put(k, column.getMetadata());
        });
        return map;
    }

    @Override
    public Set<String> getColumnKeys() {
        return Sets.newHashSet(columns.keySet());
    }

    @Override
    public Column getColumn(String key) {
        return columns.get(key);
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("size", this.getColumnNumber());
        map.put("byteSize", this.byteSize);
        map.put("data", this.columns);
        return map.toString();
    }

    @Override
    public long getColumnNumber() {
        return this.columns.size();
    }

    @Override
    public long getByteSize() {
        return byteSize;
    }

    @Override
    public boolean isEmpty() {
        return columns.isEmpty();
    }


    public long getMemorySize(){
        return oneselfMemorySize;
    }

    /**
     * 减数据大小
     * @param column 要减去的列
     */
    private void decrByteSize(final Column column) {
        if (null == column) {
            return;
        }

        byteSize -= column.getByteSize();

        // 内存的占用是column对象的头 再加实际大小
        oneselfMemorySize = oneselfMemorySize -  ClassSize.ColumnHead - column.getByteSize();
    }

    /**
     * 增加数据大小
     * @param column 要新增数据大小的列
     */
    private void incrByteSize(final Column column) {
        if (null == column) {
            return;
        }

        byteSize += column.getByteSize();

        // 内存的占用是column对象的头 再加实际大小
        oneselfMemorySize = oneselfMemorySize + ClassSize.ColumnHead + column.getByteSize();
    }

}
