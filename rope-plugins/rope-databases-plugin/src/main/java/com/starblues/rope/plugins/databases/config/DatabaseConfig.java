package com.starblues.rope.plugins.databases.config;

import lombok.Data;

import java.util.Properties;

/**
 * 数据库配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
public class DatabaseConfig {

    /**
     * 全局唯一key
     */
    private String key;

    /**
     * 配置名称
     */
    private String name;

    /***
     * 当前数据库驱动jar包路径。绝对路径
     */
    private String jarPath;

    /**
     * druid数据库连接池配置
     */
    private Properties config;

}
