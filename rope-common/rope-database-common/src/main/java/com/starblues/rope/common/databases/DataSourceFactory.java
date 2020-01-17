package com.starblues.rope.common.databases;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据库连接池工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface DataSourceFactory {

    /**
     * 创建 DataSource
     * @param key 唯一的key
     * @param properties 配置文件
     * @return DataSource
     * @throws Exception 异常
     */
    DataSource create(String key, Properties properties) throws Exception;

    /**
     * 关闭 DataSource
     * @param key 唯一的key
     */
    void close(String key);

    /**
     * 关闭所有的 DataSource
     */
    void closeAll();

}
