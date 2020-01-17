package com.starblues.rope.core.input.manager.support;

import com.starblues.rope.core.common.manager.AbstractManager;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.input.support.reader.PeriodAcquireReaderInput;
import com.starblues.rope.core.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 周期性获取数据的输入管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PeriodAcquireInputManager extends AbstractManager<PeriodAcquireReaderInput> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Transport transport;
    private final ScheduledExecutorService executor;


    public PeriodAcquireInputManager(Transport transport,
                                     int corePoolSize,
                                     ThreadFactory threadFactory) {
        this.transport = Objects.requireNonNull(transport, "transport can't be null");
        if(corePoolSize < 0){
            corePoolSize = 0;
        }
        executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);

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
    protected void toStart(PeriodAcquireReaderInput managed) throws Exception {
        managed.start(transport);
        ConfigParameter configParameter = managed.configParameter();
        if(configParameter instanceof PeriodAcquireReaderInput.Param){
            PeriodAcquireReaderInput.Param param = (PeriodAcquireReaderInput.Param) configParameter;
            ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(managed,
                    param.getDelay(), param.getPeriod(), param.getTimeUnit());
            managed.setScheduledFuture(scheduledFuture);
        }
    }

    @Override
    protected void toStop(PeriodAcquireReaderInput managed) throws Exception {
        managed.stop();
        ScheduledFuture<?> scheduledFuture = managed.getScheduledFuture();
        if(scheduledFuture != null){
            scheduledFuture.cancel(true);
        }
    }
}
