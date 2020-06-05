package com.starblues.rope.core.input.support.accept;

import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.model.record.RecordGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


import static com.starblues.rope.rest.input.HttpAcceptInputRouter.*;

/**
 * 抽象的http 接受型输入
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-04
 */
public abstract class AbstractHttpAcceptInput<Source> extends AbstractAcceptConverterInput<Source> {


    @Value("${server.port}")
    private String port;

    private final static Logger logger = LoggerFactory.getLogger(AbstractHttpAcceptInput.class);

    public AbstractHttpAcceptInput(ConverterFactory converterFactory) {
        super(converterFactory);
    }

    @Override
    protected RecordGroup customConvert(Source sourceMessage) {
        return null;
    }

    @Override
    protected void toStart(Consumer consumer) throws Exception {
        logger.info("Http json accept : url(http://ip:{}{}) in process[{}]",
                port, PATH + "[" + BODY_ACCEPT_PATH + "]/" + processId(), processId());
    }


    @Override
    protected void toStop() throws Exception {

    }

    @Override
    public void consumeMessage(Source sourceMessage) {
        super.consumeMessage(sourceMessage);
    }
}
