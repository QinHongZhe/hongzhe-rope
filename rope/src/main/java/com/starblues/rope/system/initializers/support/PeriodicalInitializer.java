package com.starblues.rope.system.initializers.support;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.google.common.base.Stopwatch;
import com.starblues.rope.system.initializers.AbstractInitializer;
import com.starblues.rope.system.periodical.AbstractPeriodical;
import com.starblues.rope.system.periodical.PeriodicalFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 周期性任务初始化者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class PeriodicalInitializer extends AbstractInitializer {

    private final PluginApplication pluginApplication;
    private final PeriodicalFactory periodicalFactory;


    public PeriodicalInitializer(PluginApplication pluginApplication,
                                 PeriodicalFactory periodicalFactory) {
        this.pluginApplication = pluginApplication;
        this.periodicalFactory = periodicalFactory;
    }


    @Override
    protected String name() {
        return "periodical";
    }

    @Override
    protected void start() throws Exception {
        PluginUser pluginUser = pluginApplication.getPluginUser();
        List<AbstractPeriodical> abstractPeriodicals = pluginUser.getBeans(AbstractPeriodical.class);
        for (AbstractPeriodical periodical : abstractPeriodicals) {
            try {
                periodical.initialize();
                periodicalFactory.registerAndStart(periodical);
            } catch (Exception e) {
                log.error("Could not initialize '{}' periodical.", periodical.getClass().getCanonicalName(), e);
            }
        }
    }

    @Override
    protected void stop() throws Exception {
        List<AbstractPeriodical> abstractPeriodicals = periodicalFactory.getAllStoppedOnGracefulShutdown();
        for (AbstractPeriodical periodical : abstractPeriodicals) {
            Stopwatch s = Stopwatch.createStarted();

            // Cancel future executions.
            Map<AbstractPeriodical, ScheduledFuture> futures = periodicalFactory.getFutures();
            if (futures.containsKey(periodical)) {
                futures.get(periodical).cancel(false);

                s.stop();
                log.info("Shutdown periodical [{}] complete, took <{}ms>.",
                        periodical.getClass().getCanonicalName(), s.elapsed(TimeUnit.MILLISECONDS));
            } else {
                log.error("Could not find periodical [{}] in futures list. Not stopping execution.",
                        periodical.getClass().getCanonicalName());
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return log;
    }


}
