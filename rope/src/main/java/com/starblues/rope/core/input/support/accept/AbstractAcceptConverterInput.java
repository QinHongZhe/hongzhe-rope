package com.starblues.rope.core.input.support.accept;

import com.starblues.rope.core.converter.AbstractInputConverter;
import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 抽象的接受数据的输入。等待别人给其发送数据。可以配置转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractAcceptConverterInput<Source> extends AbstractAcceptInput  {

    private final ConverterFactory converterFactory;
    private final Class<Source> sourceClass;

    private AbstractInputConverter inputConverter;


    public AbstractAcceptConverterInput(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
        Type type = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        sourceClass = (Class)params[0];
    }


    @Override
    public final void initialize() throws Exception {
        Class<Source> sourceMessageType = sourceMessageType();
        inputConverter = converterFactory.getInputConverter(processId(), sourceMessageType);
        init();
    }

    /**
     * 初始化
     * @throws Exception 初始化异常
     */
    public abstract void init() throws Exception;


    /**
     * 源数据类型
     * @return Class
     */
    protected Class<Source> sourceMessageType(){
        return sourceClass;
    }

    /**
     * 自定义转换数据
     * @param sourceMessage 源消息
     * @return 转换后的结果
     */
    protected abstract RecordGroup customConvert(Source sourceMessage);

    /**
     * 消费消息。子类调用即可
     * @param sourceMessage 源消息
     */
    protected void consumeMessage(Source sourceMessage){
        RecordGroup recordGroup;
        if(inputConverter == null){
            recordGroup = customConvert(sourceMessage);
        } else {
            recordGroup = inputConverter.convert(sourceMessage);
        }
        if(recordGroup != null && !recordGroup.isEmpty()){
            getConsumer().accept(recordGroup);
        }
    }


}
