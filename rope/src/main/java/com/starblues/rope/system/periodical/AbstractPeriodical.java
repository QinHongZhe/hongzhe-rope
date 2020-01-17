package com.starblues.rope.system.periodical;

import org.slf4j.Logger;

/**
 * 系统定时任务
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractPeriodical implements Runnable{

    /**
     * 在启动时定义这个线程应该定期调用还是只调用一次
     *
     * @return 周期执行 true。只执行一次 false
     */
    public abstract boolean runsForever();

    /**
     * 是否优雅的停止该周期
     *
     * @return  boolean
     */
    public abstract boolean stopOnGracefulShutdown();


    /**
     * 是否作为守护进程线程运行
     *
     * @return true。守护线程、false 非守护线程
     */
    public abstract boolean isDaemon();

    /**
     * 启动延迟秒数
     * @return 返回延迟执行的秒数
     */
    public abstract int getDelaySeconds();

    /**
     * 定义周期数
     * @return 周期执行的秒数
     */
    public abstract int getPeriodSeconds();

    public void initialize() {
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (RuntimeException e) {
            getLogger().error("Uncaught exception in periodical", e);
        }
    }

    /**
     * 返回日志打印
     * @return Logger
     */
    protected abstract Logger getLogger();

    /**
     * 子类周期运行的实现体
     */
    protected abstract void doRun();

    /**
     * 并行数
     * @return 默认 1 个
     */
    public int getParallelism() {
        return 1;
    }

}
