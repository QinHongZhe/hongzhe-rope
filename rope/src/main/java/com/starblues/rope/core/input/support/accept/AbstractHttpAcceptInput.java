package com.starblues.rope.core.input.support.accept;

import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.model.record.RecordGroup;

/**
 * 抽象的http 接受型输入
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-04
 */
public abstract class AbstractHttpAcceptInput<Source> extends AbstractAcceptConverterInput<Source> {


    public AbstractHttpAcceptInput(ConverterFactory converterFactory) {
        super(converterFactory);
    }

    @Override
    protected RecordGroup customConvert(Source sourceMessage) {
        return null;
    }

    @Override
    protected void toStart(Consumer consumer) throws Exception {

    }

    @Override
    protected void toStop() throws Exception {

    }

    @Override
    public void consumeMessage(Source sourceMessage) {
        super.consumeMessage(sourceMessage);
    }
}
