package com.starblues.rope.core.transport.input;

import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.model.record.DefaultRecord;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import com.starblues.rope.core.transport.AbstractTransport;
import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.process.factory.ProcessFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * 默认的数据输入传输器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class DefaultInputTransport extends AbstractTransport {

    public static final String ID = "default";

    private final ProcessFactory processFactory;
    private final boolean isMetric;

    public DefaultInputTransport(ProcessFactory processFactory,
                                 Boolean isMetric) {
        this.processFactory = Objects.requireNonNull(processFactory,
                "processFactory can't be null");
        if(isMetric != null){
            this.isMetric = isMetric;
        } else {
            this.isMetric = true;
        }
    }


    public DefaultInputTransport(ProcessFactory processFactory) {
        this(processFactory, true);
    }


    @Override
    public void input(RecordWrapper recordWrapper){
        String processId = recordWrapper.getProcessId();
        ProcessManager processManager = processFactory.getProcessManager(processId);
        if(processManager == null){
            log.debug("Not found processManager of process id {}", processId);
            return;
        }
        Transport outputTransport = processFactory.getOutputTransport();
        if(outputTransport == null){
            log.debug("Not found outputTransport of process id {}", processId);
            return;
        }

        if(isMetric){
            metric(recordWrapper);
        }

        DateHandler dateHandler = processManager.getDateHandler();
        if(dateHandler == null){
            outputTransport.input(recordWrapper);
            return;
        }
        try {
            RecordGroup recordGroup = recordWrapper.getRecordGroup();
            if(recordGroup == null){
                return;
            }
            List<Record> records = recordGroup.getRecords();
            if(records == null || records.isEmpty()){
                log.debug("ProcessInfo id {} records is empty", processId);
                return;
            }
            if(recordWrapper.getRecordType() == DefaultRecord.class){
                // 只有默认的记录才能进入数据矗立着
                RecordGroup handleRecordGroup = new RecordGroup();
                for (Record record : records) {
                    Record handleRecord = dateHandler.handle(record);
                    if(handleRecord != null){
                        handleRecordGroup.addRecord(handleRecord);
                    }
                }
                recordWrapper.setRecordGroup(handleRecordGroup);
            }
            outputTransport.input(recordWrapper);
        } catch (Exception e) {
            log.error("The DateHandler '{}' handle record ['']  failed in the process '{}'. " +
                            "And discarded this record. {}",
                    dateHandler.id(), processId, e.getMessage(), e);
        }
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
        return "默认的输入数据传入器";
    }

    @Override
    protected void startAfter() throws Exception {
    }

    @Override
    protected void stopAfter() throws Exception {

    }

    @Override
    protected String metricGroup() {
        return MetricsConstant.GROUP_INPUT_TRANSPORT;
    }

    @Override
    protected Logger logger() {
        return log;
    }

}
