package com.starblues.rope.core.converter;

import com.starblues.rope.core.model.record.Record;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 输出数据转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractWriterConverter<Target> implements Converter<Record, Target>{

    private final Class<Target> targetClass;


    public AbstractWriterConverter(){
        Type type = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        targetClass = (Class)params[0];
    }


    /**
     * 转换
     * @param record 记录
     * @return 目标数据
     */
    @Override
    public abstract Target convert(Record record);


    @Override
    public Class<Target> targetClass() {
        return targetClass;
    }

    /**
     * 源类型的默认实现
     * @return Record.class
     */
    @Override
    public Class<Record> sourceClass(){
        return Record.class;
    }
}
