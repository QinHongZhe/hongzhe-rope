package com.starblues.rope.core.input;

import lombok.Data;

import java.util.Properties;

/**
 * 输入管理者的配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class InputManagerConfig {

    private OneInput oneInput;
    private PeriodAcquireInput periodAcquire;
    private Properties quartzProp;

    /**
     * 一次性输入的的配置
     */
    public static class OneInput{

        public static final int DEFAULT_THREAD_NUMBER = 2;

        /**
         * 线程数
         */
        private int threadNumber = DEFAULT_THREAD_NUMBER;

        public int getThreadNumber() {
            return threadNumber;
        }

        public void setThreadNumber(int threadNumber) {
            this.threadNumber = threadNumber;
        }
    }

    /**
     * 周期性输入的配置
     */
    public static class PeriodAcquireInput{

        private int corePoolSize = 0;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

    }



}
