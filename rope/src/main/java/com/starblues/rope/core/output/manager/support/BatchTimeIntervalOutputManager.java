package com.starblues.rope.core.output.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.output.support.BatchTimeIntervalOutput;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 批量、时间间隔输出管理器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
public class BatchTimeIntervalOutputManager extends AbstractManager<BatchTimeIntervalOutput> {

    private final ScheduledExecutorService executor;


    public BatchTimeIntervalOutputManager(int corePoolSize, ThreadFactory threadFactory) {
        if(corePoolSize < 0){
            corePoolSize = 0;
        }
        executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }


    @Override
    protected String name() {
        return "BatchTimeIntervalOutput";
    }

    @Override
    protected void toStart(BatchTimeIntervalOutput managed) throws Exception {
        managed.setExecutorService(executor);
        managed.start();
    }

    @Override
    protected void toStop(BatchTimeIntervalOutput managed) throws Exception {
        managed.stop();
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
