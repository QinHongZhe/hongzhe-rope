package com.starblues.rope.process;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.input.Input;
import com.starblues.rope.core.output.Output;
import com.starblues.rope.core.handler.DateHandler;
import com.starblues.rope.core.common.config.ProcessConfig;

/**
 * 流程管理者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ProcessManager {


    /**
     * 启动流程管理器
     * @throws Exception 启动异常
     */
    void start() throws Exception;

    /**
     * 停止输入
     * @throws Exception 停止异常
     */
    void stop() throws Exception;

    /**
     * 得到当前流程的id
     * @return 流程id
     */
    String getProcessId();

    /**
     * 得到当前流程的配置
     * @return 流程配置
     */
    ProcessConfig getProcessConfig();


    /**
     * 当前流程管理器的状态
     * @return 状态
     */
    State state();

    /**
     * 得到该流程的输入
     * @return 输入
     */
    Input getInput();

    /**
     * 得到该流程的数据处理器
     * @return 数据转换器
     */
    DateHandler getDateHandler();

    /**
     * 得到该流程的数据输出
     * @return 数据
     */
    Output getOutput();

}
