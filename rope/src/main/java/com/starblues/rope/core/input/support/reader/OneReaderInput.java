package com.starblues.rope.core.input.support.reader;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.input.AbstractReaderInput;
import com.starblues.rope.core.input.reader.consumer.Consumer;
import com.starblues.rope.core.model.record.LastRecordSigner;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 只读取数据一次的输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OneReaderInput extends AbstractReaderInput {

    public static final String ID = "one";


    @Override
    protected void startAfter(Consumer consumer) throws Exception {
        super.readRecord();
        if(state() == State.RUNNING){
            // 如果是运行状态, 则新增最后一条记录的标记
            consumer.accept(new LastRecordSigner());
        }
    }


    @Override
    public void initialize() throws Exception {}

    @Override
    public ConfigParameter configParameter() {
        return null;
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "一次输入";
    }

    @Override
    public String describe() {
        return "只执行一次的输入";
    }


}
