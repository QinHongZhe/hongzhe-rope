package com.starblues.rope.process.store.support;

import com.google.gson.Gson;
import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.factory.ProcessFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * json 文件流程存储
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class JsonFileProcessStorage extends AbstractFileProcessStorage {

    public static final String ID = "json-file";

    private final Gson gson;

    public JsonFileProcessStorage(ProcessFactory processFactory,
                                  Gson gson) {
        super(processFactory);
        this.gson = gson;
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "json文件存储的流程信息";
    }

    @Override
    public String describe() {
        return "主要管理json配置的流程文件, 将流程信息存储在json文件中";
    }


    @Override
    protected void initializeProcess() {
        Path storePath = Paths.get(super.storePath);
        try {
            Files.list(storePath)
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        return "json".equalsIgnoreCase(suffix);
                    })
                    .filter(path -> {
                        try {
                            return Files.size(path) > 0;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            InputStreamReader reader = new InputStreamReader(Files.newInputStream(path),
                                    StandardCharsets.UTF_8);
                            ProcessConfig processConfig = gson.fromJson(reader, ProcessConfig.class);
                            if(processConfig == null){
                                log.error("Load json file '{}' error. Config is empty", path.toString());
                                return;
                            }
                            processStorage.create(processConfig);
                        } catch (Exception e){
                            log.error("Load json file '{}' error. {}", path.toString(), e.getMessage(), e);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeFile(File file, ProcessConfig processConfig) throws Exception {
        String json = gson.toJson(processConfig);
        Files.write(file.toPath(), json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
    }

    @Override
    protected Param instanceParam() {
        return new Param();
    }


    @Override
    protected String getFileName(ProcessConfig processConfig) {
        return processConfig.getProcessId() + ".json";
    }

    @Override
    public Logger getLogger() {
        return log;
    }


    @Getter
    class Param extends AbstractFileProcessStorage.Param{

    }

}
