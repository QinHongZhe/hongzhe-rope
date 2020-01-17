package com.starblues.rope.plugins.basic.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class FileUtils {

    private FileUtils(){

    }

    /**
     * 得到存在的文件
     * @param filePath 文件路径String
     * @return 文件路径 Path
     * @throws IOException IO 异常
     */
    public static Path getExistFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if(!Files.exists(parent)){
            Files.createDirectories(parent);
        }
        if(!Files.exists(path)){
            Files.createFile(path);
        }
        return path;
    }


}
