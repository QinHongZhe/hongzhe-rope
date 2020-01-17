package com.starblues.rope.process.store.support;

import com.starblues.rope.core.common.config.ProcessConfig;
import com.starblues.rope.process.factory.ProcessFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * yml 文件流程存储
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class YmlFileProcessStorage extends AbstractFileProcessStorage {

    public static final String ID = "yml-file";
    private final Yaml yaml = new Yaml();

    public YmlFileProcessStorage(ProcessFactory processFactory) {
        super(processFactory);
    }


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return "yml文件存储的流程信息";
    }

    @Override
    public String describe() {
        return "主要管理yml配置的流程文件, 将流程信息存储在yml文件中";
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
                        return "yml".equalsIgnoreCase(suffix) || "yaml".equalsIgnoreCase(suffix);
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
                            InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(path),
                                    StandardCharsets.UTF_8);
                            ProcessConfig processConfig = yaml.load(inputStreamReader);
                            processStorage.create(processConfig);
                        } catch (Exception e){
                            log.error("Load yml file '{}' error. {}", path.toString(), e.getMessage(), e);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Param instanceParam() {
        return new Param();
    }

    @Override
    protected void writeFile(File file, ProcessConfig processConfig) throws Exception {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(file.toPath()),
                StandardCharsets.UTF_8);
        yaml.dump(processConfig, outputStreamWriter);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }


    @Override
    protected String getFileName(ProcessConfig processConfig) {
        return processConfig.getProcessId() + ".yml";
    }

    @Override
    public Logger getLogger() {
        return log;
    }


    @Getter
    class Param extends AbstractFileProcessStorage.Param{

    }

}
