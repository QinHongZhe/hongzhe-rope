package com.starblues.rope.core.output.support;

import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.output.AbstractOutput;
import com.starblues.rope.core.output.OutputDataWrapper;
import com.starblues.rope.core.output.writer.Writer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 简单的输出。消息进入就进行输出
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SimpleOutput extends AbstractOutput {

    public static final String ID = "simple";

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public void output(RecordWrapper recordWrapper) {
        emptyWriteWarn();
        if(canNotOutput()){
            return;
        }
        if(!recordWrapper.isUserRecord()){
            // 如果不是用户的数据, 则进行返回
            return;
        }
        List<Record> records = recordWrapper.getRecordGroup().getRecords();
        if(records == null || records.isEmpty()){
            return;
        }
        for (WriterWrapper writerWrapper : writerWrappers) {
            Writer writer = writerWrapper.getWriter();
            try {
                OutputDataWrapper convertData = convertRecords(writer, recordWrapper);
                write(writerWrapper, convertData);
            } catch (Exception e) {
                outputException(writer, e);
            }
        }
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "simple";
    }

    @Override
    public String describe() {
        return "简单的输出";
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public ConfigParameter configParameter() {
        return null;
    }

}
