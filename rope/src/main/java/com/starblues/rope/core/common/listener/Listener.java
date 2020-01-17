package com.starblues.rope.core.common.listener;

import com.starblues.rope.core.common.manager.Managed;

import java.util.function.Consumer;

/**
 * 用于输入、输出启动的监听者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Listener<T extends Managed> {


    /**
     * 启动成功
     * @param id 操作的id
     */
    void success(String id);


    /**
     * 启动失败
     * @param id 输入id
     * @param e 异常
     * @return 失败处理函数。
     */
    Consumer<T> failure(String id, Throwable e);


}
