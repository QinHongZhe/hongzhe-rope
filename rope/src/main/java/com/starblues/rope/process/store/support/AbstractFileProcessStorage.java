package com.starblues.rope.process.store.support;

import com.starblues.rope.core.common.ChildLogger;
import com.starblues.rope.core.common.param.ConfigParam;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.factory.ProcessFactory;
import com.starblues.rope.process.store.ProcessStorage;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 抽象的流程存储者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractFileProcessStorage implements ProcessStorage, ChildLogger {

    protected final Param param;

    protected String storePath;

    protected final ProcessStorage processStorage;


    protected AbstractFileProcessStorage(ProcessFactory processFactory) {
        this.processStorage = new MemoryProcessStorage(processFactory);
        param = instanceParam();
    }

    @Override
    public void initialize() throws Exception {
        String storePath = param.getStorePath();
        Path path = Paths.get(storePath);
        if(!Files.exists(path)){
            getLogger().info("Create path '{}' success", storePath);
            Files.createDirectories(path);
        }
        this.storePath = path.toString();
        getLogger().info("Start initialize '{}' processStorage in '{}' path", id(), storePath);
        initializeProcess();
    }

    /**
     * 初始化流程
     */
    protected abstract void initializeProcess();




    @Override
    public void create(ProcessConfig processConfig) throws Exception {
        File file = null;
        try {
            file = getProcessConfigFile(processConfig, true);
            writeFile(file, processConfig);
            processStorage.create(processConfig);
        } catch (Exception e){
            // 出现异常, 则判断文件是否存在, 如果存在则删除
            if(file != null){
                file.deleteOnExit();
            }
            throw e;
        }
    }


    @Override
    public boolean exist(String processId) {
        return getProcessInfo(processId) != null;
    }

    @Override
    public void delete(String processId) throws Exception {
        ProcessInfo processInfo = getProcessInfo(processId);
        if(processId == null){
            return;
        }
        ProcessConfig processConfig = processInfo.getProcessConfig();
        if(processConfig == null){
            return;
        }
        try {
            File processConfigFile = getProcessConfigFile(processConfig, false);
            processConfigFile.delete();
        } finally {
            processStorage.delete(processId);
        }
    }

    /**
     * 得到配置文件
     * @param processConfig 流程配置bean
     * @param create 文件不存在时, 是否创建文件
     * @return File
     * @throws IOException IOException
     */
    protected File getProcessConfigFile(ProcessConfig processConfig, boolean create) throws IOException {
        File file = new File(storePath + File.separator + getFileName(processConfig));
        if(create && !file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    @Override
    public void update(ProcessConfig processConfig) throws Exception {
        File processConfigFile = getProcessConfigFile(processConfig, true);
        writeFile(processConfigFile, processConfig);
        processStorage.update(processConfig);
    }

    @Override
    public ProcessInfo getProcessInfo(String processId) {
        return processStorage.getProcessInfo(processId);
    }


    @Override
    public List<ProcessInfo> getProcessInfos() {
        return processStorage.getProcessInfos();
    }

    /**
     * 配置文件
     * @return 配置
     */
    @Override
    public Param configParameter(){
        return param;
    }

    /**
     * 实例配置文件
     * @return Param 的实现类
     */
    protected abstract Param instanceParam();


    /**
     * 向文件写入内容
     * @param file 配置文件
     * @param processConfig processConfig
     * @throws Exception 写入文件异常
     */
    protected abstract void writeFile(File file, ProcessConfig processConfig) throws Exception;


    /**
     * 得到文件名称
     * @param processConfig processConfig
     * @return 文件名称
     */
    protected abstract String getFileName(ProcessConfig processConfig);


    @Getter
    static abstract class Param implements ConfigParameter {

        private static final String STORE_PATH = "storePath";

        private String storePath;

        @Override
        public void parsing(ConfigParamInfo configParamInfo) throws Exception {
            storePath = configParamInfo.getString(STORE_PATH, "./");
        }

        @Override
        public ConfigParam configParam() {
            return null;
        }
    }

}
