package com.starblues.rope.core.output;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;

import java.util.concurrent.ThreadFactory;

/**
 * 输入管理者的配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class OutputManagerConfig {


    private TimeIntervalOutput timeInterval;


    /**
     * 周期性输入的配置
     */
    public static class TimeIntervalOutput{

        private int corePoolSize = 0;
        private ThreadFactory threadFactory;

        public TimeIntervalOutput(){
            threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("TimeIntervalOutput-Pool-%d")
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
