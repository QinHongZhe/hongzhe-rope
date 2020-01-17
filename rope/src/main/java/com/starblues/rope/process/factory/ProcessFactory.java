package com.starblues.rope.process.factory;

import com.starblues.rope.core.common.State;
import com.starblues.rope.core.transport.Transport;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.config.Configuration;

import java.util.List;

/**
 * 流程工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ProcessFactory {

    /**
     * 启动输入工厂
     * @param configuration 配置文件
     * @throws Exception 启动异常
     */
    void start(Configuration configuration) throws Exception;

    /**
     * 停止输入工厂
     * @throws Exception 停止异常
     */
    void stop() throws Exception;

    /**
     * 当前工厂的状态
     * @return 输入的状态
     */
    State state();

    /**
     * 得到输入的 Transport
     * @return Transport
     */
    Transport getInputTransport();

    /**
     * 得到输出的 Transport
     * @return Transport
     */
    Transport getOutputTransport();


    /**
     * 创建一个流程.不启动
     * @param processConfig 流程配置
     * @return 流程管理器
     * @throws Exception 创建异常
     */
    ProcessManager create(ProcessConfig processConfig) throws Exception;

    /**
     * 启动流程
     * @param processId 流程id
     * @return 流程管理器
     * @throws Exception 启动异常
     */
    ProcessManager start(String processId) throws Exception;


    /**
     * 停止一个流程
     * @param processId 流程id
     * @throws Exception 停止异常
     */
    void stop(String processId) throws Exception;

    /**
     * 移除一个流程
     * @param processId 流程id
     * @throws Exception 移除异常
     */
    void remove(String processId) throws Exception;


    /**
     * 得到流程管理器
     * @param processId 流程id
     * @return ProcessManager
     */
    ProcessManager getProcessManager(String processId);

    /**
     * 是否存在该流程
     * @param processId 流程id
     * @return 存在返回true. 不存在返回false
     */
    boolean exist(String processId);


    /**
     * 得到所有的运行流程管理器
     * @return ProcessManager 集合
     */
    List<ProcessManager> getRunProcessManager();

    /**
     * 得到所有的停止流程管理器
     * @return ProcessManager 集合
     */
    List<ProcessManager> getStopProcessManager();


}
