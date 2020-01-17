package com.starblues.rope.system.initializers.support;

import com.starblues.rope.system.initializers.AbstractInitializer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

/**
 * 存储库初始化者
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Slf4j
@Component
public class RepositoryInitializer extends AbstractInitializer {

    @Value("${spring.datasource.url}")
    private String rootDir;


    @Override
    protected String name() {
        return "repository";
    }

    @Override
    protected void start() throws Exception {
        if(rootDir == null){
            rootDir = "";
        } else {
            int index = rootDir.indexOf(":");
            index = rootDir.indexOf(":", index + 1);
            rootDir = rootDir.substring(index + 1);
        }

        File file = new File(rootDir);
        if(!file.exists()){
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                Files.createDirectories(parentFile.toPath());
            }
        }
    }

    @Override
    protected void stop() throws Exception {

    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
}
