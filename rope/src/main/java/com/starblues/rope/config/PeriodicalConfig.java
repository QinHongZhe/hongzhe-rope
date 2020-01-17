package com.starblues.rope.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.starblues.rope.system.periodical.LogUncaughtExceptionHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "periodical")
@Slf4j
public class PeriodicalConfig {

    private static final int SCHEDULED_THREADS_POOL_SIZE = 30;

    @Value("${periodicalPoll:30}")
    public Integer periodicalPoll;

    @Value("${periodicalPoll:30}")
    public Integer periodicalDaemonPoll;

    @Bean("periodicalExecutor")
    public ScheduledExecutorService scheduledExecutorService(){

        if(periodicalPoll == null || periodicalPoll < 0){
            periodicalPoll = SCHEDULED_THREADS_POOL_SIZE;
        }

        return Executors.newScheduledThreadPool(periodicalPoll,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduled-%d")
                        .setDaemon(false)
                        .setUncaughtExceptionHandler(new LogUncaughtExceptionHandler(log))
                        .build()
        );
    }

    @Bean("periodicalDaemonExecutor")
    public ScheduledExecutorService daemonScheduled(){

        if(periodicalDaemonPoll == null || periodicalDaemonPoll < 0){
            periodicalDaemonPoll = SCHEDULED_THREADS_POOL_SIZE;
        }

        return Executors.newScheduledThreadPool(periodicalDaemonPoll,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduled-daemon-%d")
                        .setDaemon(true)
                        .setUncaughtExceptionHandler(new LogUncaughtExceptionHandler(log))
                        .build()
        );
    }

}
