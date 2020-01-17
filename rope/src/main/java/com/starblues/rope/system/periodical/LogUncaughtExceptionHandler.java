package com.starblues.rope.system.periodical;

import org.slf4j.Logger;

/**
 * 线程日志异常处理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{
    private final Logger log;

    public LogUncaughtExceptionHandler(Logger log) {
        this.log = log;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Thread {} failed by not catching exception: {}.", t.getName(), e);
    }
}
