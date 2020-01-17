package com.starblues.rope.core.output.writer;

import com.starblues.rope.core.converter.Converter;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.utils.CommonUtils;
import com.starblues.rope.core.converter.AbstractWriterConverter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 抽象的写入者。可设置转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractConverterWriter<ConvertData>
        implements Writer<ConvertData>{

    private AbstractWriterConverter writerConverter;

    private final Class<ConvertData> convertDataClass;

    public AbstractConverterWriter(){
        Type[] params = CommonUtils.getParameterizedType(getClass());
        convertDataClass = (Class)params[0];
    }


    public void setWriterConverter(AbstractWriterConverter writerConverter){
        this.writerConverter = writerConverter;
    }

    public AbstractWriterConverter getWriterConverter() {
        return writerConverter;
    }


    @Override
    public boolean support(Converter converter) {
        if(converter == null){
            return false;
        }
        return Objects.equals(converter.targetClass(), convertDataClass);
    }

    @Override
    public ConvertData convert(Record record) throws Exception {
        if(writerConverter == null){
            return customConvert(record);
        }
        Object convert = writerConverter.convert(record);
        if(convert == null){
            throw new NullPointerException("Converter '" + writerConverter.id() + "' convert result is null");
        }
        if(Objects.equals(convert.getClass(), convertDataClass)){
            return (ConvertData) convert;
        } else {
            throw new RuntimeException("The type of the result of the converter '" + writerConverter.id() + "' conversion " +
                    "does not match the type specified by the current class");
        }
    }

    @Override
    public Class<ConvertData> convertDataClass() {
        return convertDataClass;
    }

    /**
     * 自定义转换.如果没有设置写入者转换器, 则启用自定义转换器
     * @param record 数据记录
     * @return 自定义转换的结果
     * @throws Exception 转换异常
     */
    protected abstract ConvertData customConvert(Record record) throws Exception;


}
