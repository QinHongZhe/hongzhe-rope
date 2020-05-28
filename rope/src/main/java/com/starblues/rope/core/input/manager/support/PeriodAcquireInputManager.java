package com.starblues.rope.core.input.manager.support;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.input.InputManagerConfig;
import com.starblues.rope.core.input.support.reader.PeriodAcquireReaderInput;
import com.starblues.rope.core.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 周期性获取数据的输入管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PeriodAcquireInputManager extends AbstractManager<PeriodAcquireReaderInput> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Transport transport;
    private final InputManagerConfig.PeriodAcquireInput periodAcquireInput;

    private volatile ScheduledExecutorService executor;


    public PeriodAcquireInputManager(Transport transport,
                                     InputManagerConfig.PeriodAcquireInput periodAcquireInput) {
        this.transport = Objects.requireNonNull(transport, "transport can't be null");
        this.periodAcquireInput = Objects.requireNonNull(periodAcquireInput, "periodAcquireInput can't be null");
    }


    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected String name() {
        return "PeriodAcquireInputManager";
    }

    @Override
    protected synchronized void toStart(PeriodAcquireReaderInput managed) throws Exception {
        checkAndInitialize();
        managed.start(transport);
        ConfigParameter configParameter = managed.configParameter();
        if(configParameter instanceof PeriodAcquireReaderInput.Param){
            PeriodAcquireReaderInput.Param param = (PeriodAcquireReaderInput.Param) configParameter;
            ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(managed,
                    param.getDelay(), param.getPeriod(), param.getTimeUnit());
            managed.setScheduledFuture(scheduledFuture);
        }
    }

    private void checkAndInitialize() {
        if(executor == null){
            int corePoolSize = periodAcquireInput.getCorePoolSize();
            if(corePoolSize < 0){
                corePoolSize = 0;
            }
            ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("PeriodAcquireReaderInput-Pool-%d")
                    .build();
            executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
        }
    }

    @Override
    protected synchronized void toStop(PeriodAcquireReaderInput managed) throws Exception {
        managed.stop();
        ScheduledFuture<?> scheduledFuture = managed.getScheduledFuture();
        if(scheduledFuture != null){
            scheduledFuture.cancel(true);
        }
    }


}
