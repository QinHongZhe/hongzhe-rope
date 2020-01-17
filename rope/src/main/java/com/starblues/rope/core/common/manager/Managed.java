package com.starblues.rope.core.common.manager;

import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.param.ConfigParameter;

/**
 * 被管理者的对象
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Managed extends Identity {


    /**
     * 流程id. 该输出作用于的流程id
     * @return 流程id
     */
    String processId();

    /**
     * 初始化被管理者
     * @throws Exception 初始化异常
     */
    void initialize() throws Exception;


    /**
     * 当前被管理者的状态
     * @return 状态
     */
    State state();


    /**
     * 参数配置者
     * @return ConfigParameter 的实现
     */
    ConfigParameter configParameter();

}
