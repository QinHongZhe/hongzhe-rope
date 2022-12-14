package com.starblues.rope.core.input.reader.consumer;

import com.starblues.rope.config.StaticBean;
import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.metrics.ThroughputMetric;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.utils.MetricUtils;

import java.util.List;

/**
 * Transport 数据消费者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class TransportConsumer extends AbstractStateConsumer {


    private final String processId;
    private final Transport transport;

    private ThroughputMetric throughputMetric;

    public TransportConsumer(String processId,
                             Transport transport) {
        this.processId = processId;
        this.transport = transport;
    }


    @Override
    public String processId() {
        return processId;
    }


    @Override
    protected void startup() throws Exception {
        throughputMetric = new ThroughputMetric(MetricsConstant.GROUP_INPUT_READER, processId);
        MetricUtils.safelyRegister(StaticBean.metricRegistry, throughputMetric);
    }

    @Override
    protected void shutdown() throws Exception {
        StaticBean.metricRegistry.remove(throughputMetric.byteCounterName());
        StaticBean.metricRegistry.remove(throughputMetric.countCounterName());
    }

    @Override
    protected void acceptImpl(Record record) {
        if(record == null){
            return;
        }
        RecordWrapper recordWrapper = new RecordWrapper();
        recordWrapper.setProcessId(processId);
        RecordGroup group = new RecordGroup();
        group.addRecord(record);
        recordWrapper.setRecordGroup(group);
        recordWrapper.setRecordType(record.getClass());
        throughputMetric.countCounter().inc();
        throughputMetric.byteCounter().inc(record.getByteSize());
        transport.input(recordWrapper);
    }

    @Override
    protected void acceptImpl(RecordGroup recordGroup) {
        if(recordGroup == null){
            return;
        }
        List<Record> records = recordGroup.getRecords();
        if(records != null && !records.isEmpty()){
            RecordWrapper recordWrapper = new RecordWrapper();
            recordWrapper.setProcessId(processId);
            recordWrapper.setRecordGroup(recordGroup);

            // 取第一条记录设置记录的类型
            Record record = records.get(0);
            recordWrapper.setRecordType(record.getClass());

            throughputMetric.countCounter().inc(recordGroup.size());
            throughputMetric.byteCounter().inc(recordGroup.getByteSize());
            transport.input(recordWrapper);
        }
    }


}
