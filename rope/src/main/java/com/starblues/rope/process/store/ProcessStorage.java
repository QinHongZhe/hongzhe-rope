package com.starblues.rope.process.store;

import com.starblues.rope.core.common.Identity;
import com.starblues.rope.core.common.State;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.config.ProcessConfig;
import lombok.Data;

import java.util.List;

/**
 * 流程存储接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ProcessStorage extends Identity {


    /**
     * 初始化配置
     * @throws Exception 初始化异常
     */
    void initialize() throws Exception;


    /**
     * 创建流程
     * @param processConfig 流程配置
     * @throws Exception 创建流程异常
     */
    void create(ProcessConfig processConfig) throws Exception;

    /**
     * 删除流程
     * @param processId 流程id
     * @throws Exception 删除流程异常
     */
    void delete(String processId) throws Exception;


    /**
     * 修改流程信息
     * @param processConfig 流程配置
     * @throws Exception 修改流程异常
     */
    void update(ProcessConfig processConfig) throws Exception;

    /**
     * 得到流程信息
     * @param processId 流程id
     * @return ProcessInfo
     */
    ProcessInfo getProcessInfo(String processId);


    /**
     * 得到该存储方式的所有流程信息
     * @return ProcessInfo
     */
    List<ProcessInfo> getProcessInfos();


    /**
     * 参数配置者
     * @return ConfigParameter 的实现
     */
    ConfigParameter configParameter();

    /**
     * 流程信息
     */
    @Data
    class ProcessInfo{

        /**
         * 存储者id
         */
        private String storageId;

        /**
         * 流程配置
         */
        private ProcessConfig processConfig;

        /**
         * 流程状态
         */
        private String state = State.NEW.toString();
    }

}
