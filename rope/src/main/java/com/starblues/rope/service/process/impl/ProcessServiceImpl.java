package com.starblues.rope.service.process.impl;

import com.google.common.collect.Lists;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.ProcessManager;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.service.process.ProcessService;
import com.starblues.rope.service.process.model.ProcessCount;
import com.starblues.rope.service.process.model.ProcessInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程服务实现类
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class ProcessServiceImpl implements ProcessService {

    private final ProcessFactory processFactory;

    public ProcessServiceImpl(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }


    @Override
    public List<ProcessInfo> getAllProcess() {
        List<ProcessInfo> processInfos = Lists.newArrayList();
        processInfos.addAll(getStopProcess());
        processInfos.addAll(getRunProcess());
        return processInfos;
    }

    @Override
    public List<ProcessInfo> getRunProcess() {
        return processFactory.getRunProcessManager().stream()
                .map(processManager -> getProcessInfo(processManager))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessInfo> getStopProcess() {
        return processFactory.getStopProcessManager().stream()
                .map(processManager -> getProcessInfo(processManager))
                .collect(Collectors.toList());
    }

    @Override
    public ProcessInfo getProcessById(String processId) {
        ProcessManager processManager = processFactory.getProcessManager(processId);
        return getProcessInfo(processManager);
    }

    @Override
    public void start(String processId) throws Exception {
        processFactory.start(processId);
    }

    @Override
    public void stop(String processId) throws Exception {
        processFactory.stop(processId);
    }

    @Override
    public void remove(String processId) throws Exception {
        processFactory.remove(processId);
    }

    @Override
    public ProcessCount count() {
        List<ProcessManager> runProcessManager = processFactory.getRunProcessManager();
        List<ProcessManager> stopProcessManager = processFactory.getStopProcessManager();

        ProcessCount processCount = new ProcessCount();
        if(runProcessManager != null){
            processCount.setStartCount(runProcessManager.size());
        }
        if(stopProcessManager != null){
            processCount.setStopCount(stopProcessManager.size());
        }
        processCount.setCount(processCount.getStartCount() + processCount.getStopCount());
        return processCount;
    }


    /**
     * 得到流程信息
     * @param processManager 流程管理者
     * @return 流程信息
     */
    private ProcessInfo getProcessInfo(ProcessManager processManager){
        if(processManager == null){
            return null;
        }
        ProcessInfo.ProcessInfoBuilder builder = ProcessInfo.builder()
                .processId(processManager.getProcessId())
                .state(processManager.state());
        ProcessConfig processConfig = processManager.getProcessConfig();
        if(processConfig != null){
            builder = builder.name(processConfig.getName());
        }
        return builder.build();
    }

}
