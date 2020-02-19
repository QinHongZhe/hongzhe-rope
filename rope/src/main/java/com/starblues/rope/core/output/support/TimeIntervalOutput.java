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

import java.util.concurrent.*;

/**
 * 定时输出.每个设定时间间隔，执行输出.
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class TimeIntervalOutput extends AbstractCacheOutput implements Runnable{

    public static final String ID = "time-interval";

    private ScheduledFuture<?> scheduledFuture;
    private final Param param;

    public TimeIntervalOutput() {
        param = new Param();
    }


    @Override
    protected void outputEvent() {

    }


    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "时间间隔输出";
    }

    @Override
    public String describe() {
        return "指定时间间隔输出";
    }

    @Override
    public void run() {
        try {
            if(!isRunning()){
                // 不是运行状态
                return;
            }
            outputToWrite();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void toStop() throws Exception {
        if(scheduledFuture != null){
            scheduledFuture.cancel(true);
        }
        if(outputCount() > 0){
            outputToWrite();
        }
    }


    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
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

        private final static String TIME_INTERVAL = "timeInterval";
        private final static Integer DEFAULT_TIME_INTERVAL = 60;

        private Integer timeInterval = DEFAULT_TIME_INTERVAL;


        @Override
        public void parsing(ConfigParamInfo configParamInfo) throws Exception {
            Integer timeInterval = configParamInfo.getInt(TIME_INTERVAL, DEFAULT_TIME_INTERVAL);
            if(timeInterval != null && timeInterval > 0){
                this.timeInterval = timeInterval;
            }
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    NumberField.toBuilder(TIME_INTERVAL, "时间间隔", DEFAULT_TIME_INTERVAL)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("当达到时间间隔, 无论设定大小是多少, 都进行批量输出。单位: 秒")
                            .build()
            );

            return configParam;
        }
    }


}
