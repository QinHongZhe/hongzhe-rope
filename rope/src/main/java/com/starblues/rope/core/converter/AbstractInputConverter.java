package com.starblues.rope.core.converter;

import com.starblues.rope.core.model.record.Record;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 输入转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractInputConverter<Source> implements Converter<Source, Record>{

    private final Class<Source> sourceClass;


    public AbstractInputConverter(){
        Type type = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        sourceClass = (Class)params[0];
    }


    /**
     * 转换
     * @param source 源数据
     * @return 目标数据
     */
    @Override
    public abstract Record convert(Source source);


    @Override
    public Class<Source> sourceClass() {
        return sourceClass;
    }

    @Override
    public Class<Record> targetClass(){
        return Record.class;
    }

}
