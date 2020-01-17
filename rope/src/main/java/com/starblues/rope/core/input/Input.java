package com.starblues.rope.core.input;

import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.common.manager.Managed;
import com.starblues.rope.core.transport.Transport;

/**
 * 读取数据者的接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface Input extends Managed, Identity {


    /**
     * 启动输入
     * @param transport 数据运输器
     * @throws Exception 启动异常
     */
    void start(Transport transport) throws Exception;

    /**
     * 停止输入
     * @throws Exception 停止异常
     */
    void stop() throws Exception;


}
