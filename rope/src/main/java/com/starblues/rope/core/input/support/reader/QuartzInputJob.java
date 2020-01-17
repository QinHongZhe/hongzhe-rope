package com.starblues.rope.core.input.support.reader;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * QuartzReaderInput 的任务类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class QuartzInputJob implements Job {

    public static final String QUARTZ_INPUT = "QuartzReaderInput";

    private QuartzReaderInput quartzInput;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        initParam(jobDataMap);
        quartzInput.readRecord();
    }

    /**
     * 初始化参数
     * @param jobDataMap job 数据Map
     */
    private void initParam(JobDataMap jobDataMap){
        this.quartzInput = (QuartzReaderInput) jobDataMap.get(QUARTZ_INPUT);
    }

}
