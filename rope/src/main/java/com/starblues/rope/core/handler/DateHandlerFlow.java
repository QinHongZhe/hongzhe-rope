package com.starblues.rope.core.handler;

import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.model.record.Record;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 转换流
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class DateHandlerFlow implements DateHandler {

    private final static String ID = "DateHandlerFlow";

    private final List<DateHandler> dateHandlers;

    public DateHandlerFlow(List<DateHandler> dateHandlers) {
        this.dateHandlers = dateHandlers;
    }

    @Override
    public void initialize(String processId) throws Exception {
        // no thing
    }

    @Override
    public Record handle(Record record) {
        if(dateHandlers == null || dateHandlers.isEmpty()){
            return record;
        }
        for (DateHandler dateHandler : dateHandlers) {
            try {
                record = dateHandler.handle(record);
                if(record == null){
                    // 如果返回的记录为null。表示丢弃该消息
                    return null;
                }
            } catch (Exception e) {
                log.error("The DateHandler '{}' handle record ['']  failed. " +
                                "And discarded this record. {}",
                        dateHandler.id(), e.getMessage(), e);
                return null;
            }
        }
        return record;
    }

    @Override
    public void destroy() throws Exception {
        if(dateHandlers == null || dateHandlers.isEmpty()){
            return;
        }
        for (DateHandler dateHandler : dateHandlers) {
            if(dateHandler == null){
                continue;
            }
            try {
                dateHandler.destroy();
            } catch (Exception e){
                log.error("DateHandler '{}' destroy failure. {}", dateHandler.id(), e.getMessage(), e);
            }
        }
    }

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
        return ID;
    }

    @Override
    public String describe() {
        return "数据处理流";
    }
}
