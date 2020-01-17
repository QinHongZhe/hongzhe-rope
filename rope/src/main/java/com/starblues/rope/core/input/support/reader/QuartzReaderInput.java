package com.starblues.rope.core.input.support.reader;

import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.param.fields.NumberField;
import com.starblues.rope.core.common.param.fields.TextField;
import com.starblues.rope.core.input.AbstractReaderInput;
import com.starblues.rope.utils.ExceptionMsgUtils;
import com.starblues.rope.utils.IDUtils;
import lombok.Getter;
import org.quartz.Job;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * quartz 定时任务输入
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QuartzReaderInput extends AbstractReaderInput {

    public static final String ID = "quartz";

    private final Param param;

    public QuartzReaderInput() {
        this.param = new Param();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public Param configParameter() {
        return param;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "周期输入(quartz)";
    }

    @Override
    public String describe() {
        return "基于quartz框架的定时任务输入";
    }

    /**
     * 得到 job 的class
     * @return Class
     */
    public Class<? extends Job> getJobClass(){
        return QuartzInputJob.class;
    }

    /**
     * 得到传入job的数据集合
     * @return Map<String,Object>
     */
    public Map<String,Object> jobData(){
        Map<String,Object> jobData = new HashMap<>();
        jobData.put(QuartzInputJob.QUARTZ_INPUT, this);
        return jobData;
    }


    @Getter
    public static class Param implements ConfigParameter{

        public static final String JOB_NAME = "jobName";
        public static final String TRIGGER_NAME = "triggerName";
        public static final String TRIGGER_CRON = "triggerCron";
        public static final String DELAY_SECONDS = "delaySeconds";

        /**
         * 任务名称
         */
        private String jobName;

        /**
         * 触发器的名称
         */
        private String triggerName;

        /**
         * 触发器定时规则Cron表达式
         */
        private String triggerCron;

        /**
         * 延迟执行的描述
         */
        private Integer delaySeconds = 0;


        @Override
        public void parsing(ConfigParamInfo configParamInfo) throws Exception {
            String jobName = configParamInfo.getString(JOB_NAME);
            if(StringUtils.isEmpty(jobName)){
                jobName = IDUtils.uuid();
            }
            this.jobName = jobName;

            String triggerName = configParamInfo.getString(TRIGGER_NAME);
            if(StringUtils.isEmpty(triggerName)){
                triggerName = IDUtils.uuid();
            }
            this.triggerName = triggerName;


            String triggerCron = configParamInfo.getString(TRIGGER_CRON);
            if(StringUtils.isEmpty(triggerCron)){
                throw ExceptionMsgUtils.getInputParamException(ID, TRIGGER_CRON);
            }
            this.triggerCron = triggerCron;

            Integer delaySeconds = configParamInfo.getInt(DELAY_SECONDS);
            if(delaySeconds != null && delaySeconds < 0L){
                throw ExceptionMsgUtils.getInputParamException(ID, TRIGGER_CRON, "can't be less than 0L");
            } else if(delaySeconds == null){
                delaySeconds = 0;
            }
            this.delaySeconds = delaySeconds;
        }

        @Override
        public ConfigParam configParam() {
            ConfigParam configParam = new ConfigParam();

            configParam.addField(
                    TextField.toBuilder(TRIGGER_CRON, "表达式", "")
                            .required(true)
                            .description("Quartz表达式")
                            .build()
            );


            configParam.addField(
                    NumberField.toBuilder(DELAY_SECONDS, "延迟秒数", 0)
                            .required(true)
                            .attribute(NumberField.Attribute.ONLY_POSITIVE)
                            .description("单位秒")
                            .build()
            );


            configParam.addField(
                    TextField.toBuilder(JOB_NAME, "任务名称", "")
                            .required(false)
                            .description("任务名称")
                            .build()
            );

            configParam.addField(
                    TextField.toBuilder(TRIGGER_NAME, "调度器名称", "")
                            .required(false)
                            .description("调度器名称")
                            .build()
            );


            return configParam;
        }
    }


}
