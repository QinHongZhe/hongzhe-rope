package com.starblues.rope.system.periodical;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 周期性任务工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
@Component
public class PeriodicalFactory {

    private final List<AbstractPeriodical> periodicals;
    private final Map<AbstractPeriodical, ScheduledFuture> futures;
    private final ScheduledExecutorService scheduler;
    private final ScheduledExecutorService daemonScheduler;


    public PeriodicalFactory(@Qualifier("periodicalExecutor") ScheduledExecutorService scheduler,
                             @Qualifier("periodicalDaemonExecutor") ScheduledExecutorService daemonScheduler) {
        this.scheduler = scheduler;
        this.daemonScheduler = daemonScheduler;
        this.periodicals = Lists.newArrayList();
        this.futures = Maps.newHashMap();
    }

    public synchronized void registerAndStart(AbstractPeriodical periodical) {
        if (!periodical.runsForever()) {
            log.info("Starting [{}] periodical, running forever.", periodical.getClass().getCanonicalName());

            for (int i = 0; i < periodical.getParallelism(); i++) {
                Thread t = new Thread(periodical);
                t.setDaemon(periodical.isDaemon());
                t.setName("periodical-" + periodical.getClass().getCanonicalName() + "-" + i);
                t.setUncaughtExceptionHandler(new LogUncaughtExceptionHandler(log));
                t.start();
            }
        } else {
            log.info(
                    "Starting [{}] periodical in [{}s], polling every [{}s].",
                    periodical.getClass().getCanonicalName(),
                    periodical.getDelaySeconds(),
                    periodical.getPeriodSeconds());

            ScheduledExecutorService scheduler = periodical.isDaemon() ? this.daemonScheduler : this.scheduler;
            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                    periodical,
                    periodical.getDelaySeconds(),
                    periodical.getPeriodSeconds(),
                    TimeUnit.SECONDS
            );

            futures.put(periodical, future);
        }

        periodicals.add(periodical);
    }


    public List<AbstractPeriodical> getAll() {
        return Lists.newArrayList(periodicals);
    }

    /**
     * 获取可优雅关闭的 AbstractPeriodical
     * @return 拷贝 AbstractPeriodical
     */
    public List<AbstractPeriodical> getAllStoppedOnGracefulShutdown() {
        List<AbstractPeriodical> result = Lists.newArrayList();
        for (AbstractPeriodical periodical : periodicals) {
            if (periodical.stopOnGracefulShutdown()) {
                result.add(periodical);
            }
        }

        return result;
    }


    public Map<AbstractPeriodical, ScheduledFuture> getFutures() {
        return Maps.newHashMap(futures);
    }

}
