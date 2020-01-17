package com.starblues.rope.core.input.support.accept;

import com.starblues.rope.core.converter.AbstractInputConverter;
import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.model.record.Record;

/**
 * 抽象的接受数据的输入。等待别人给其发送数据。可以配置转换器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractAcceptConverterInput<Source> extends AbstractAcceptInput  {

    private final ConverterFactory converterFactory;

    private AbstractInputConverter inputConverter;

    public AbstractAcceptConverterInput(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }


    @Override
    public void initialize() throws Exception {
        Class<Source> sourceMessageType = sourceMessageType();
        inputConverter = converterFactory.getInputConverter(processId(), sourceMessageType);
    }

    /**
     * 源数据类型
     * @return Class
     */
    protected abstract Class<Source> sourceMessageType();

    /**
     * 自定义转换数据
     * @param sourceMessage 源消息
     * @return 转换后的结果
     */
    protected abstract Record customConvert(Source sourceMessage);

    /**
     * 消费消息。子类调用即可
     * @param sourceMessage 源消息
     */
    protected void consumeMessage(Source sourceMessage){
        Record record;
        if(inputConverter == null){
            record = customConvert(sourceMessage);
        } else {
            record = inputConverter.convert(sourceMessage);
        }
        if(!record.isEmpty()){
            getConsumer().accept(record);
        }
    }


}
