package com.starblues.rope.process.store;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.starblues.rope.config.configuration.RopeCoreConfiguration;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.starblues.rope.utils.CommonUtils.getImpls;

/**
 * 流程存储工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class ProcessStorageFactory {

    private final Map<String, ProcessStorage> initializeProcessStorage = Maps.newHashMap();

    private final AtomicBoolean isInit = new AtomicBoolean(false);
    private final PluginApplication pluginApplication;
    private final List<RopeCoreConfiguration.ProcessStorageConfig> processStorageConfigs;


    private PluginUser pluginUser;

    public ProcessStorageFactory(PluginApplication pluginApplication,
                                 RopeCoreConfiguration dataTransferConfiguration) {
        this.pluginApplication = pluginApplication;
        this.processStorageConfigs = dataTransferConfiguration.getProcessStorages();
    }

    /**
     * 初始化流程配置
     */
    public synchronized void initialize(){
        if(processStorageConfigs == null || processStorageConfigs.isEmpty()){
            return;
        }
        if(isInit.get()){
            log.warn("ProcessStorageFactory already initialized");
            return;
        }
        this.pluginUser = pluginApplication.getPluginUser();
        for (RopeCoreConfiguration.ProcessStorageConfig processStorageConfig : processStorageConfigs) {
            if(processStorageConfig == null){
                continue;
            }
            String id = processStorageConfig.getId();
            if(StringUtils.isEmpty(id)){
                continue;
            }
            ProcessStorage processStorage = getImpls(pluginUser, ProcessStorage.class, impl -> {
                return Objects.equals(impl.id(), id);
            });
            if(processStorage == null){
                log.error("Not found processStorage id '{}'", id);
                continue;
            }
            ConfigParameter configParameter = processStorage.configParameter();
            if(configParameter != null){
                Map<String, Object> params = processStorageConfig.getParams();
                if(params == null || params.isEmpty()){
                    continue;
                }
                ConfigParamInfo configParamInfo = new ConfigParamInfo(params);
                try {
                    configParameter.parsing(configParamInfo);
                    initializeProcessStorage.put(id, processStorage);
                } catch (Exception e) {
                    log.error("Parsing processStorage '{}' config error", id, e);
                }
            } else {
                initializeProcessStorage.put(id, processStorage);
            }
            try {
                processStorage.initialize();
                log.info("Initialize processStorage '{}' success", id);
            } catch (Exception e) {
                log.error("Initialize processStorage '{}' error", id, e);
            }
        }
        isInit.set(true);
    }

    /**
     * 得到系统配置的流程存储的信息
     * @return ProcessStorageInfo 集合
     */
    public List<ProcessStorageInfo> getProcessStorageInfo(){
        return initializeProcessStorage.values().stream()
                .map(processStorage -> {
                    ProcessStorageInfo processStorageInfo = new ProcessStorageInfo();
                    processStorageInfo.setDescribe(processStorage.describe());
                    processStorageInfo.setId(processStorage.id());
                    processStorageInfo.setName(processStorage.name());
                    return processStorageInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 得到全部的 ProcessStorage
     * @return List
     */
    public List<ProcessStorage> getProcessStorage(){
        return ImmutableList.copyOf(initializeProcessStorage.values());
    }

    /**
     * 根据配置的id获取 ProcessStorage
     * @param id id
     * @return ProcessStorage
     */
    public ProcessStorage getProcessStorage(String id){
        if(StringUtils.isEmpty(id)){
            throw new IllegalArgumentException("Id cannot be empty");
        }
        return initializeProcessStorage.get(id);
    }



    @Data
    public static class ProcessStorageInfo{
        private String id;
        private String name;
        private String describe;
    }

}
