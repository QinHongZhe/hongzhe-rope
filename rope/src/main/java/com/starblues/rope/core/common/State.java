package com.starblues.rope.core.common;

/**
 * 状态
 *
 * @author zhangzhuo
 * @version 1.0
 */
public enum State {

    /**
     * 新建状态
     */
    NEW,

    /**
     * 启动中状态
     */
    STARTING,

    /**
     * 运行中状态
     */
    RUNNING,

    /**
     * 停止中状态
     */
    STOPPING,

    /**
     * 停止状态
     */
    STOP,

    /**
     * 错误状态
     */
    FAILED
}
