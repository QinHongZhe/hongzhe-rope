package com.starblues.rope.core.common.manager;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.listener.Listener;

import java.util.Set;

/**
 * 管理者
 *
 * @author zhangzhuo
 * @version 1.0
 * @param <T> 实现 Managed 的子类
 */
public interface Manager<T extends Managed> {

    /**
     * 是否支持该管理者管理的对象
     * @param object 被管理的对象
     * @return 支持返回true, 不支持返回false
     */
    boolean support(Object object);


    /**
     * 添加被管理的对象
     * @param t 被管理的对象
     * @throws Exception 添加输t出异常
     */
    void add(T t) throws Exception;


    /**
     * 存在被管理的对象
     * @param id 被管理的对象Id
     * @return true 存在, false 失败
     */
    boolean exist(String id);


    /**
     * 如果该被管理的对象已经被添加到管理器中, 可以通过被管理的对象id启动
     * @param id 被管理的对象Id
     * @throws Exception 启动异常
     */
    void start(String id) throws Exception;

    /**
     * 直接启动被管理的对象
     * @param t 被管理的对象
     * @throws Exception 启动异常
     */
    void start(T t) throws Exception;


    /**
     * 停止被管理的对象
     * @param id 被管理的对象Id
     * @throws Exception 停止异常
     */
    void stop(String id) throws Exception;

    /**
     * 移除被管理的对象Id
     * @param id 被管理的对象Id
     * @throws  Exception 移除异常
     */
    void remove(String id) throws Exception;


    /**
     * 启动所有被管理的对象
     * @param listener 监听者。用于监听每一个被管理者的启动结果
     */
    void startAll(Listener<T> listener);

    /**
     * 停止所有被管理的对象
     * @param listener 监听者。用于监听每一个被管理者的停止结果
     */
    void stopAll(Listener<T> listener);

    /**
     * 得到被管理的对象的状态
     * @param  id 被管理的对象Id
     * @return 返回状态
     */
    State getState(String id);

    /**
     * 得到被管理的对象
     * @param  id 被管理的对象Id
     * @return T 被管理的对象
     */
    T get(String id);

    /**
     * 得到所有被管理对象的id
     * @return 被管理的对象id集合
     */
    Set<String> getIds();

}
