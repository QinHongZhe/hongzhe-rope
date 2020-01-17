package com.starblues.rope.core.input.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.input.support.reader.QuartzReaderInput;
import com.starblues.rope.core.transport.Transport;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Quartz 输入的管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class QuartzInputManager extends AbstractManager<QuartzReaderInput> {

    private String triggerGroup = "QuartzInputTriggerGroup";
    private String jobGroup = "QuartzInputJobGroup";

    private final Transport transport;
    private final Scheduler scheduler;


    public QuartzInputManager(Transport transport,
                              SchedulerFactory schedulerFactory) throws SchedulerException {
        this.transport = Objects.requireNonNull(transport, "transport can't be null");
        Objects.requireNonNull(schedulerFactory, "schedulerFactory can't be null");
        this.scheduler = schedulerFactory.getScheduler();
    }

    /**
     * 可设置触发器分组名称
     * @param triggerGroup 触发器分组名称.不可为空
     */
    public void setTriggerGroup(String triggerGroup) {
        if(StringUtils.isEmpty(triggerGroup)){
            return;
        }
        this.triggerGroup = triggerGroup;
    }

    /**
     * 可设置任务分组名称
     * @param jobGroup 任务分组名称.不可为空
     */
    public void setJobGroup(String jobGroup) {
        if(StringUtils.isEmpty(jobGroup)){
            return;
        }
        this.jobGroup = jobGroup;
    }


    @Override
    public Logger getLogger() {
        return log;
    }



    @Override
    protected String name() {
        return "QuartzInputManager";
    }

    @Override
    protected void toStart(QuartzReaderInput managed) throws Exception {
        QuartzReaderInput.Param param = managed.configParameter();
        JobDetail jobDetail = configJobDetail(param, managed.getJobClass(), managed.jobData());
        Trigger trigger = configTrigger(param);
        if (scheduler.checkExists(JobKey.jobKey(param.getJobName(), jobGroup))) {
            throw new Exception("The quartz input job " + param.getJobName() + "already exists");
        }
        managed.start(transport);
        scheduler.scheduleJob(jobDetail, trigger);
        synchronized (scheduler) {
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        }
    }

    @Override
    protected void toStop(QuartzReaderInput managed) throws Exception {
        QuartzReaderInput.Param param = managed.configParameter();

        String jobName = param.getJobName();
        String triggerName = param.getTriggerName();
        // 停止触发器
        scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        // 停止任务
        scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));

        // 停止该触发器的任务
        scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroup));
        // 删除任务
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
    }


    /**
     * 配置触发器
     * @param param 参数
     * @return 触发器
     */
    private Trigger configTrigger(QuartzReaderInput.Param param) {
        //0 56 09 ? * *
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(param.getTriggerName(), triggerGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(param.getTriggerCron()));

        Integer delaySeconds = param.getDelaySeconds();
        if(delaySeconds <= 0L){
            triggerBuilder.startNow();
        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            localDateTime = localDateTime.plusSeconds(60);
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            triggerBuilder.startAt(date);
        }
        return triggerBuilder.build();
    }

    /**
     * 配置任务详细
     * @param param 参数
     * @param jobData 任务数据
     * @return  JobDetail
     */
    private JobDetail configJobDetail(QuartzReaderInput.Param param,
                                      Class<? extends Job> jobClass,
                                      Map<String,Object> jobData) {
        JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
                .withIdentity(param.getJobName(), jobGroup);
        if(jobData != null){
            jobBuilder.setJobData(new JobDataMap(jobData));
        }
        return jobBuilder.build();
    }

}
