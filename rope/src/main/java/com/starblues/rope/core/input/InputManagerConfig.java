package com.starblues.rope.core.input;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;

import java.util.Properties;
import java.util.concurrent.ThreadFactory;

/**
 * 输入管理者的配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class InputManagerConfig {


    private PeriodAcquireInput periodAcquire;
    private Properties quartz;

    /**
     * 周期性输入的配置
     */
    public static class PeriodAcquireInput{

        private int corePoolSize = 0;
        private ThreadFactory threadFactory;

        public PeriodAcquireInput(){
            threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("PeriodAcquireReaderInput-Pool-%d")
                    .build();
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public ThreadFactory getThreadFactory() {
            return threadFactory;
        }
    }



}
