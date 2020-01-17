package com.starblues.rope.service.process;

import com.starblues.rope.service.process.model.ProcessCount;
import com.starblues.rope.service.process.model.ProcessInfo;

import java.util.List;

/**
 * 流程服务层
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface ProcessService {


    /**
     * 得到全部的流程信息
     * @return 流程信息集合
     */
    List<ProcessInfo> getAllProcess();


    /**
     * 得到运行中的流程信息
     * @return 流程信息集合
     */
    List<ProcessInfo> getRunProcess();

    /**
     * 得到停止的流程信息
     * @return 流程信息集合
     */
    List<ProcessInfo> getStopProcess();


    /**
     * 通过流程id获取流程信息
     * @param processId 流程id
     * @return 流程信息
     */
    ProcessInfo getProcessById(String processId);

    /**
     * 启动流程
     * @param processId 流程id
     * @throws Exception 启动异常
     */
    void start(String processId) throws Exception;

    /**
     * 停止流程
     * @param processId 流程id
     * @throws Exception 停止异常
     */
    void stop(String processId) throws Exception;


    /**
     * 移除流程
     * @param processId 流程id
     * @throws Exception 停止异常
     */
    void remove(String processId) throws Exception;

    /**
     * 流程统计
     * @return ProcessCount
     */
    ProcessCount count();

}
