package com.starblues.rope.core.output.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.output.support.TimeIntervalOutput;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.concurrent.*;

/**
 * TimeIntervalOutput 的管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class TimeIntervalOutputManager extends AbstractManager<TimeIntervalOutput> {

    private final ScheduledExecutorService executor;


    public TimeIntervalOutputManager(int corePoolSize,
                                     ThreadFactory threadFactory) {
        if(corePoolSize < 0){
            corePoolSize = 0;
        }
        executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }


    @Override
    protected String name() {
        return "TimeIntervalOutput";
    }

    @Override
    protected void toStart(TimeIntervalOutput managed) throws Exception {
        managed.start();
        TimeIntervalOutput.Param param = managed.configParameter();
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(managed,
                param.getTimeInterval(), param.getTimeInterval(), TimeUnit.SECONDS);
        managed.setScheduledFuture(scheduledFuture);
    }

    @Override
    protected void toStop(TimeIntervalOutput managed) throws Exception {
        managed.stop();
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
