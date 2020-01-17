package com.starblues.rope.core.output;

import com.google.common.collect.Lists;
import com.starblues.rope.config.StaticBean;
import com.starblues.rope.config.constant.MetricsConstant;
import com.starblues.rope.core.common.ChildLogger;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.StateControl;
import com.starblues.rope.core.metrics.ThroughputMetric;
import com.starblues.rope.core.model.RecordWrapper;
import com.starblues.rope.core.model.record.Record;
import com.starblues.rope.core.model.record.RecordGroup;
import com.starblues.rope.core.output.writer.BaseWriterConfigParameter;
import com.starblues.rope.core.output.writer.DoNotOperateWriter;
import com.starblues.rope.core.output.writer.Writer;
import com.starblues.rope.utils.CommonUtils;
import com.starblues.rope.utils.MetricUtils;
import lombok.Data;
import org.apache.lucene.util.RamUsageEstimator;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 抽象的输出类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractOutput implements Output, ChildLogger {


    private final StateControl stateControl = new StateControl();


    /**
     * 流程id
     */
    private String processId;


    /**
     * 写入者的线程池
     */
    private ExecutorService writerExecutorService;

    /**
     * 输出写入者列表集合。
     */
    protected final List<WriterWrapper> writerWrappers = Lists.newArrayList();

    /**
     * 存储每个输出写入者的信息.key 为写入者的code
     */
    protected final Map<String, OutputWriter> outputWriters = new ConcurrentHashMap<>();

    /**
     * 设置流程id
     * @param processId 流程id
     */
    public void setProcessId(String processId) {
        if(StringUtils.isEmpty(this.processId) && !StringUtils.isEmpty(processId)){
            this.processId = processId;
        }
    }

    /**
     * 设置写入者的线程池
     * @param executorService executorService
     */
    public void setExecutorService(ExecutorService executorService) {
        if(this.writerExecutorService == null && executorService != null){
            this.writerExecutorService = executorService;
        }
    }



    @Override
    public String processId() {
        return processId;
    }

    @Override
    public final void start() throws Exception {
        stateControl.start();
        try {
            if(writerExecutorService == null){
                throw new RuntimeException("No Setting to setExecutorService in process:" + processId);
            }
            startBefore();
            stateControl.startSuccessful();
            startAfter();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }

    @Override
    public final void stop() throws Exception {
        stateControl.stop();
        try {
            for (WriterWrapper writerWrapper : writerWrappers) {
                Writer writer = writerWrapper.getWriter();
                try {
                    writer.destroy();
                } catch (Exception e){
                    getLogger().error("Writer '{}' destroy failure in process[{}]. {}", writer.id(),
                            processId,
                            e.getMessage(), e);
                }
            }
            toStop();
            stateControl.stopSuccessful();
        } catch (Exception e){
            stateControl.throwable(e);
            throw e;
        }
    }


    @Override
    public final State state() {
        return stateControl.getCurrentState();
    }

    /**
     * 添加数据写入者
     * @param writerWrapper 数据写入者包装对象
     */
    @Override
    public synchronized void addWriter(WriterWrapper writerWrapper){
        if(isRunning()){
            throw new RuntimeException("Cannot add writer. Because the output is already running in process" + processId);
        }
        if(writerWrapper == null || writerWrapper.getWriter() == null){
            return;
        }
        if(StringUtils.isEmpty(writerWrapper.getCode())){
            writerWrapper.setCode(CommonUtils.uuid());
        }
        if(outputWriters.containsKey(writerWrapper.getCode())){
            throw new RuntimeException("The writer code['" + writerWrapper.getCode() +
                    "'] already exists in process" + processId);
        }
        this.writerWrappers.add(writerWrapper);
        OutputWriter outputWriter = new OutputWriter();
        outputWriter.setWriterWrapper(writerWrapper);
        outputWriter.setThroughputMetric(createWriterThroughputMetrics(writerWrapper));

        BaseWriterConfigParameter parameter = writerWrapper.getWriter().configParameter();
        if(parameter == null || parameter.getParallel() == null || parameter.getParallel()){
            outputWriter.setParallel(true);
        } else {
            Lock lock = new ReentrantLock(true);
            outputWriter.setParallel(false);
            outputWriter.setLock(lock);
        }
        outputWriters.put(writerWrapper.getCode(), outputWriter);
    }



    /**
     * 添加数据写入者
     * @param writerWrappers 数据写入者包装对象集合
     */
    @Override
    public synchronized void addWriter(List<WriterWrapper> writerWrappers){
        if(isRunning()){
            throw new RuntimeException("Cannot add writer. Because the output is already running in process:" + processId);
        }
        if(writerWrappers != null && !writerWrappers.isEmpty()){
            for (WriterWrapper writerWrapper : writerWrappers) {
                if(writerWrapper == null){
                    continue;
                }
                addWriter(writerWrapper);
            }
        }
    }

    /**
     * 创建写入者的指标记录对象
     * @param writerWrapper writerWrapper
     */
    private ThroughputMetric createWriterThroughputMetrics(WriterWrapper writerWrapper){
        ThroughputMetric throughputMetric = new ThroughputMetric(MetricsConstant.GROUP_OUTPUT_WRITER,
                processId, writerWrapper.getCode());
        MetricUtils.safelyRegister(StaticBean.metricRegistry, throughputMetric);
        return throughputMetric;
    }


    @Override
    public List<WriterWrapper> getWriter() {
        return this.writerWrappers.stream()
                .peek(writerWrapper -> {
                    writerWrapper.setWriter(new DoNotOperateWriter(writerWrapper.getWriter()));
                })
                .collect(Collectors.toList());
    }


    /**
     * 转换数据格式
     * @param writer 数据写入者
     * @param recordWrapper 数据包装对象
     * @throws Exception 转换异常
     */
    protected OutputDataWrapper convertRecords(Writer writer, RecordWrapper recordWrapper) throws Exception {
        RecordGroup recordGroup = recordWrapper.getRecordGroup();
        Class convertDataClass = writer.convertDataClass();
        OutputDataWrapper convertData = new OutputDataWrapper();
        if(convertDataClass == null || convertDataClass == Record.class){
            List records = recordGroup.getRecords();
            return OutputDataWrapper.getInstance(records, recordGroup.getByteSize());
        }
        List<Object> convertObjects = new ArrayList<>();
        long byteSize = 0L;
        List<Record> records = recordGroup.getRecords();
        for (Record record : records) {
            Object convert = writer.convert(record);
            if(convert == null){
                getLogger().error("The writer's '{}' converter converts the result to null in process[{}]. Discard the record.",
                        processId, writer.id());
                continue;
            }
            if(convert instanceof Record){
                byteSize = byteSize + ((Record) convert).getByteSize();
            } else {
                byteSize = byteSize + RamUsageEstimator.sizeOfObject(convert);
            }
            convertObjects.add(convert);
        }
        convertData.setData(convertObjects);
        convertData.setByteSize(byteSize);
        return convertData;
    }


    /**
     * 启动之前. 子类可重写
     * @throws Exception 操作异常
     */
    protected void startBefore() throws Exception{

    }

    /**
     * 启动之后. 子类可重写
     * @throws Exception 操作异常
     */
    protected void startAfter() throws Exception{

    }

    /**
     * 子类对停止操作的实现. 子类可重写
     * @throws Exception 停止抛出的异常
     */
    protected void toStop() throws Exception{

    }


    /**
     * 该输入是否运行中
     * @return 运行中返回 true
     */
    protected boolean isRunning(){
        return state() == State.RUNNING;
    }

    /**
     * 写入数据公共实现
     * @param writerWrapper 写入者包装类
     * @param dataWrapper 数据包装者
     * @throws Exception 写入异常
     */
    protected void write(WriterWrapper writerWrapper,
                         OutputDataWrapper dataWrapper) throws Exception {
        if(writerWrapper == null || dataWrapper == null){
            getLogger().debug("metricWriter: writerWrapper or dataWrapper is null in process:" + processId);
            return;
        }
        Writer writer = writerWrapper.getWriter();
        if(writer == null){
            getLogger().debug("metricWriter: writer is null in process:" + processId);
            return;
        }
        OutputWriter outputWriter = outputWriters.get(writerWrapper.getCode());
        if(outputWriter == null){
            throw new RuntimeException("Not found writer code '" + writerWrapper.getCode() + "' " +
                    "in process:" + processId);
        }
        writerExecutorService.execute(()->{
            if(outputWriter.getParallel()){
                write(outputWriter, dataWrapper);
            } else {
                Lock lock = outputWriter.getLock();
                lock.lock();
                try {
                    write(outputWriter, dataWrapper);
                } finally {
                    lock.unlock();
                }
            }
        });
    }



    /**
     * 写入数据
     * @param outputWriter 输出写入者
     * @param dataWrapper 数据包装类
     */
    private void write(OutputWriter outputWriter, OutputDataWrapper dataWrapper) {
        WriterWrapper writerWrapper = outputWriter.getWriterWrapper();
        if(writerWrapper == null){
            return;
        }
        Writer writer = writerWrapper.getWriter();
        try {
            writer.write(dataWrapper.getData());
            ThroughputMetric throughputMetric = outputWriter.getThroughputMetric();
            if(throughputMetric != null){
                throughputMetric.byteCounter().inc(dataWrapper.getByteSize());
                throughputMetric.countCounter().inc(dataWrapper.getSize());
            }
        } catch (Exception e){
            outputException(writer, e);
        }
    }


    /**
     * 检查数据包装类是否存在数据
     * @param recordWrapper recordWrapper
     * @return 存在数据返回true. 不存在数据返回false
     */
    protected boolean checkRecordWrapper(RecordWrapper recordWrapper){
        if(recordWrapper == null){
            return false;
        }
        List<Record> records = recordWrapper.getRecordGroup().getRecords();
        if(records == null || records.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * 没有设置写入者时, 日志打印警告。
     * @return 为空, 返回true. 不为空返回false
     */
    protected boolean emptyWriteWarn(){
        if(writerWrappers.isEmpty()){
            getLogger().warn("Not found writers in process[{}], please make sure writer is configured. " +
                    "And this record discarded", processId);
            return true;
        }
        return false;
    }

    /**
     * 检查是否不能输出数据
     * @return 不可输出返回true. 可输出返回false
     */
    protected boolean canNotOutput(){
        if(state() == State.FAILED){
            // 错误状态, 丢弃数据
            getLogger().error("Current output '{}' is not normal in process[{}], cannot output data. " +
                            "The data is discarded. reason : {}",
                    this.getClass().getSimpleName(),
                    processId,
                    stateControl.getThrowable().getMessage());
            return true;
        }
        if(!isRunning()){
            // 如果状态为停止中或者停止状态。不在接受输出
            getLogger().error("Current output '{}' is not running in process[{}], cannot output data. " +
                    "The data is discarded. current state : {}", this.getClass().getSimpleName(), processId,
                    state());
            return true;
        }
        return false;
    }


    /**
     * 输出异常统一打印
     * @param writer 数据写入者
     * @param e 异常
     */
    protected void outputException(Writer writer,  Exception e){
        getLogger().error("The '{}' output failed in the writer[{}] of process[{}]. {}",
                id(), writer.id(), processId,  e.getMessage(), e);
    }

    /**
     * 输出的写入者
     */
    @Data
    private class OutputWriter {
        /**
         * 写入者包装类
         */
        private WriterWrapper writerWrapper;

        /**
         * 写入者的指标记录对象
         */
        private ThroughputMetric throughputMetric;

        /**
         * 该写入者是否并行写入
         */
        private Boolean parallel;

        /**
         * 如何非并行写入, 则使用该锁控制
         */
        private Lock lock;
    }


}
