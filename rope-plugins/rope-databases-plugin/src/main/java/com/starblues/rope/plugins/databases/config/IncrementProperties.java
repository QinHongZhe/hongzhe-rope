package com.starblues.rope.plugins.databases.config;

import com.google.common.base.Strings;
import com.starblues.rope.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

/**
 * @Description: 增量配置
 * @Author: xuesy
 * @Version: 1.0
 * @Create Date Time: 2019/12/11 9:42
 * @Update Date Time:
 * @see
 */
@Slf4j
public class IncrementProperties {

    private static final String INCREMENT_TYPE = "increment";
    private static final String PROPERTIES_PATH = IncrementProperties.class.getClassLoader()
                            .getResource("increment.properties").getPath();

    /**
     * 通过key 获取 配置的值
     * @param key
     * @return
     */
    public static String getIncrementValue(String key) {
        Properties props = new Properties();
        key = Objects.requireNonNull(key,
                "Increment Properties getIncrementValue param key can't be null");
        try {
            InputStream in = new FileInputStream(PROPERTIES_PATH);
            props.load(in);
            String value = props.getProperty(key);
            in.close();
            return value;
        }catch (Exception e) {
            log.warn("properties load function exception",e.getMessage());
        }
        return null;
    }

    /**
     * 更改配置文件值，没有就新增
     * @param key
     * @param value
     */
    public static void setIncrementValue(String key, String value) {
        Properties props = new Properties();
        key = Objects.requireNonNull(key,
                "Increment Properties setIncrementValue param key can't be null");
        value = Objects.requireNonNull(value,
                "Increment Properties setIncrementValue param value can't be null");
        try {
            InputStream in = new FileInputStream(PROPERTIES_PATH);
            props.load(in);
            props.setProperty(key,value);
            in.close();
            OutputStream out = new FileOutputStream(PROPERTIES_PATH);
            props.store(out,
                    "update increment value at:" + TimeUtil.getNowTimeToSeconds()
                            + "\nupdate key is:" + key);
            out.close();
        }catch (Exception e) {
            log.warn("properties load function exception",e.getMessage());
        }
    }

    /**
     * 判断是否需要增量处理,是则调用setIncrementValue
     * @param dataSync 数据同步类型
     * @param incrementField 增量字段(必须保证全局唯一，用户可以自己定义;如：流程ID.增量字段)
     * @param incrementValue 增量值
     */
    public static void incrementDeal(String dataSync, String incrementField, String incrementValue) {
        if(!Objects.equals(dataSync,INCREMENT_TYPE)) {
            return;
        }

        if(Strings.isNullOrEmpty(incrementField)) {
            log.warn("incrementField is null!");
            return;
        }
        setIncrementValue(incrementField, incrementValue);
    }
}
