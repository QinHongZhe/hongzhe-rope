package com.starblues.rope.process.store.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.process.store.ProcessStorage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 内存流程存储者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class MemoryProcessStorage implements ProcessStorage {

    private final static String ID = "memory";

    private final ProcessFactory processFactory;
    private final Map<String, ProcessConfig> processConfigMap = Maps.newConcurrentMap();

    public MemoryProcessStorage(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }


    @Override
    public void initialize() throws Exception {
        // no thing
    }

    @Override
    public void create(ProcessConfig processConfig) throws Exception {
        start(processConfig);
        processConfigMap.put(processConfig.getProcessId(), processConfig);
    }

    @Override
    public boolean exist(String processId) {
        return processConfigMap.containsKey(processId);
    }


    @Override
    public void delete(String processId) throws Exception {
        ProcessManager processManager = processFactory.getProcessManager(processId);
        if(processManager != null){
            processManager.stop();
        }
        processConfigMap.remove(processId);
    }

    @Override
    public void update(ProcessConfig processConfig) throws Exception {
        if(processConfig == null){
            return;
        }
        String processId = processConfig.getProcessId();
        processFactory.remove(processId);
        start(processConfig);
        processConfigMap.put(processId, processConfig);
    }

    @Override
    public ProcessInfo getProcessInfo(String processId) {
        if(StringUtils.isEmpty(processId)){
            return null;
        }
        return getProcessInfo(processConfigMap.get(processId));
    }

    @Override
    public List<ProcessInfo> getProcessInfos() {
        List<ProcessInfo> processInfos = Lists.newArrayList();
        Set<Map.Entry<String, ProcessConfig>> entrySet = processConfigMap.entrySet();
        for (Map.Entry<String, ProcessConfig> entry : entrySet) {
            ProcessInfo processInfo = getProcessInfo(entry.getValue());
            if(processInfo != null){
                processInfos.add(processInfo);
            }
        }
        return processInfos;
    }

    /**
     * 启动该流程
     * @param processConfig 流程配置
     * @throws Exception 启动异常
     */
    private void start(ProcessConfig processConfig) throws Exception {
        ProcessManager processManager = processFactory.create(processConfig);
        if(processManager == null){
            return;
        }
        if(processConfig.getIsStart() != null && processConfig.getIsStart()){
            processFactory.start(processManager.getProcessId());
        }
    }


    /**
     * 根据流程配置得到流程信息
     * @param processConfig 流程的配置
     * @return 流程信息
     */
    private ProcessInfo getProcessInfo(ProcessConfig processConfig){
        if(processConfig == null){
            return null;
        }
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcessConfig(processConfig);
        processInfo.setStorageId(id());
        ProcessManager processManager = processFactory.getProcessManager(processConfig.getProcessId());
        if(processManager != null){
            // 说明在流程工厂中启动
            processInfo.setState(processManager.state().toString());
        } else {
            // 说明没在流程工厂中启动
            processInfo.setState("NOT_CREATE");
        }
        return processInfo;
    }


    @Override
    public ConfigParameter configParameter() {
        return null;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "内存流程存储";
    }

    @Override
    public String describe() {
        return "将流程存储在内存中";
    }
}
