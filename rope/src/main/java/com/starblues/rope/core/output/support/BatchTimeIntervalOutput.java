package com.starblues.rope.core.output.support;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.output.AbstractCacheOutput;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 批量、时间间隔组合输出
 * - 当数据量达到设定值时，无论时间设定间隔是否执行，直接输出数据。
 * - 当达到时间设定间隔执行时，无论数据量是否达到设定值，直接输出数据。
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class BatchTimeIntervalOutput extends AbstractCacheOutput implements Runnable{

    public static final String ID = "batch-time-interval";

    /**
     * 用于记录是否正在输出中。保证批量和定时互斥。
     */
    private Lock writing = new ReentrantLock(true);
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;

    private final Param param;

    public BatchTimeIntervalOutput() {
        this.param = new Param();
    }



    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "批量、时间间隔组合输出";
    }

    @Override
    public String describe() {
        return "当数据量达到设定值时，无论时间设定间隔是否执行，直接输出数据; " +
                "当达到时间设定间隔执行时，无论数据量是否达到设定值，直接输出数据。";
    }


    @Override
    protected void outputEvent() {
        if(outputCount() >= param.getBatchSize()){
            if(!writing.tryLock()){
                // 没有获取到锁，说明定时任务正在执行。
                return;
            }
            try {
                // 获取到了锁，说明定时任务没有正在输出。该处进行批量输出。
                outputToWrite();
                // 判断是否存在定时输出，如果存在，则取消该定时
                if(scheduledFuture != null){
                    scheduledFuture.cancel(true);
                    scheduledFuture = null;
                }
                // 说明定时任务正在输出。该处不执行
            } finally {
                writing.unlock();
            }
        }
        if(scheduledFuture == null){
            scheduledFuture = executorService.schedule(this,
                    param.getTimeInterval(), TimeUnit.SECONDS);
        }
    }


    @Override
    public Logger getLogger() {
        return log;
    }


    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void run() {
        if(!isRunning()){
            return;
        }
        if(writing.tryLock()){
            // 获取到了锁, 说明非互斥。可以进行输出
            try {
                outputToWrite();
                // 设置当前任务 Future 为null
                if(scheduledFuture != null){
                    scheduledFuture = null;
                }
            } finally {
                writing.unlock();
            }
        }
    }

    @Override
    protected void toStop() throws Exception {
        if(outputCount() <= 0){
            return;
        }
        // 说明存在数据
        if(writing.tryLock()){
            // 获取到锁。非互斥
            if(scheduledFuture != null){
                // 如果定时任务存在, 则取消
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }
            outputToWrite();
        }
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public Param configParameter() {
        return param;
    }

    @Data
    public static class Param implements ConfigParameter {

        public final static String BATCH_SIZE = "batchSize";
        public final static String TIME_INTERVAL = "timeInterval";

        private final static Integer DEFAULT_BATCH_SIZE = 100;
        private final static Integer DEFAULT_TIME_INTERVAL = 60;

        private Integer batchSize = DEFAULT_BATCH_SIZE;
        private Integer timeInterval = DEFAULT_TIME_INTERVAL;


        @Override
        public void parsing(ConfigParamInfo configParamInfo) throws Exception {
            Integer batchSize = configParamInfo.getInt(BATCH_SIZE, DEFAULT_BATCH_SIZE);
            if(batchSize != null && batchSize > 0){
                this.batchSize = batchSize;
            }

            Integer timeInterval = configParamInfo.getInt(TIME_INTERVAL, DEFAULT_TIME_INTERVAL);
            if(timeInterval != null && timeInterval > 0){
                this.timeInterval = timeInterval;
            }
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();
            configParam.addField(
                    NumberField.toBuilder(BATCH_SIZE, "批量大小", DEFAULT_BATCH_SIZE)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("当达到设定大小, 无论定时是否执行, 都进行批量输出")
                            .build()
            );

            configParam.addField(
                    NumberField.toBuilder(TIME_INTERVAL, "时间间隔", DEFAULT_TIME_INTERVAL)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("当达到时间间隔, 无论设定大小是多少, 都进行批量输出, 单位: 秒")
                            .build()
            );

            return configParam;
        }
    }


}
