package com.starblues.rope.plugins.basic.input;

import com.starblues.rope.core.converter.ConverterFactory;
import com.starblues.rope.core.input.support.accept.AbstractHttpAcceptInput;
import com.starblues.rope.core.input.support.accept.BaseAcceptInputConfigParameter;
import org.springframework.stereotype.Component;

/**
 * http json 接受者的输入
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-04
 */
@Component
public class HttpJsonAcceptInput extends AbstractHttpAcceptInput<String> {

    public final static String ID = "http-json-input";

    public HttpJsonAcceptInput(ConverterFactory converterFactory) {
        super(converterFactory);
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public BaseAcceptInputConfigParameter configParameter() {
        return null;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "http 接受型输入";
    }

    @Override
    public String describe() {
        return "当前服务器附带的http 接受型输入";
    }
}
