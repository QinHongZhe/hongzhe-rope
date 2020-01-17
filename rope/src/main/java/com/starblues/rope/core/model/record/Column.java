package com.starblues.rope.core.model.record;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.lucene.util.RamUsageEstimator;

import java.util.Date;

/**
 * 数据列
 *
 * @author zhangzhuo
 * @version 1.0
 */
@EqualsAndHashCode
@ToString
public class Column {

    public static final String DEFAULT_KEY = "default";


    private Type type = Type.NULL;

    private String key;

    private Object metadata;

    private long byteSize = 0L;

    /**
     * 自动识别列数据类型和大小
     * @param key 数据key
     * @param metadata 列的元数据
     * @return 列对象
     */
    public static Column auto(String key, Object metadata){
        Column column = new Column();
        column.setMetadata(metadata);
        column.setKey(key);
        return column;
    }


    /**
     * 默认的的数据。默认key为 default
     * @param metadata 列的元数据
     * @return 列对象
     */
    public static Column defaultAuto(Object metadata){
        return auto(DEFAULT_KEY, metadata);
    }


    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{

        private Type type;
        private String key;
        private Object metadata;
        private long byteSize;

        public Builder type(Type type){
            this.type = type;
            return this;
        }

        public Builder key(String key){
            this.key = key;
            return this;
        }

        public Builder metadata(Object metadata){
            this.metadata = metadata;
            return this;
        }

        public Builder byteSize(long byteSize){
            this.byteSize = byteSize;
            return this;
        }

        public Column build(){
            Column column = new Column();
            column.setKey(key);
            column.setMetadata(metadata);
            column.setType(type);
            column.setByteSize(byteSize);
            return column;
        }

    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
        if(metadata != null){
            if(this.byteSize == 0L){
                this.byteSize = RamUsageEstimator.sizeOfObject(metadata);
            }
            if(this.type == null || this.type == Type.NULL){
                if (metadata instanceof Byte) {
                    this.type = Type.BYTE;
                } else if(metadata instanceof Short){
                    this.type = Type.SHORT;
                } else if(metadata instanceof Integer){
                    this.type = Type.INT;
                } else if (metadata instanceof Long) {
                    this.type = Type.LONG;
                } else if (metadata instanceof Float) {
                    this.type = Type.FLOAT;
                } else if (metadata instanceof Double) {
                    this.type = Type.DOUBLE;
                } else if (metadata instanceof String) {
                    this.type = Type.STRING;
                } else if (metadata instanceof Boolean) {
                    this.type = Type.BOOLEAN;
                } else if (metadata instanceof Date) {
                    this.type = Type.DATE;
                }  else {
                    this.type = Type.BAD;
                }
            }
        }

    }

    public long getByteSize() {
        return byteSize;
    }

    public void setByteSize(long byteSize) {
        this.byteSize = byteSize;
    }

    /**
     * 数据类型
     */
    public enum Type {
        /**
         * 数据类型列举
         */
        BAD, NULL, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, BOOLEAN, DATE
    }

}
