package com.starblues.rope.core.converter;

import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 输入转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractInputConverter<Source> implements Converter<Source, RecordGroup>{

    private final Class<Source> sourceClass;


    public AbstractInputConverter(){
        Type type = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        sourceClass = (Class)params[0];
    }


    /**
     * 转换
     * @param source 源数据
     * @return 记录组
     */
    @Override
    public abstract RecordGroup convert(Source source);



    @Override
    public Class<Source> sourceClass() {
        return sourceClass;
    }

    @Override
    public Class<RecordGroup> targetClass(){
        return RecordGroup.class;
    }

}
