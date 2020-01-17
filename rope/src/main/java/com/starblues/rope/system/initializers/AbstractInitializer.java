package com.starblues.rope.system.initializers;

import com.starblues.rope.core.common.StateControl;
import org.slf4j.Logger;

/**
 * 抽象的初始化者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractInitializer {

    private final StateControl stateControl = new StateControl();

    /**
     * 初始化操作
     * @throws Exception 初始化异常
     */
    public final void initialize() throws Exception{
        stateControl.start();
        try {
            start();
            stateControl.startSuccessful();
            getLogger().info("Initialize '{}' success", name());
        } catch (Exception e){
            stateControl.throwable(e);
            getLogger().info("Initialize '{}' error. {}", name(), e.getMessage());
            throw e;
        }
    }


    /**
     * 停止操作
     * @throws Exception 停止异常
     */
    public final void shutdown() throws Exception{
        stateControl.stop();
        try {
            stop();
            stateControl.stopSuccessful();
            getLogger().info("Shutdown '{}' success", name());
        } catch (Exception e){
            stateControl.throwable(e);
            getLogger().info("Shutdown '{}' error. {}", name(), e.getMessage());
            throw e;
        }
    }

    /**
     * 名称
     * @return String
     */
    protected abstract String name();

    /**
     * 启动
     * @throws Exception 启动异常
     */
    protected abstract void start() throws Exception;


    /**
     * 停止
     * @throws Exception 停止异常
     */
    protected abstract void stop() throws Exception;

    /**
     * 得到日志打印
     * @return Logger
     */
    protected abstract Logger getLogger();

}
