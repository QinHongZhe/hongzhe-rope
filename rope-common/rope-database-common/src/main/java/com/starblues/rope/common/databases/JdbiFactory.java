package com.starblues.rope.common.databases;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * jdbi工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class JdbiFactory {


    private static final Logger log = LoggerFactory.getLogger(JdbiFactory.class);
    private DataSourceFactory dataSourceFactory;
    private Map<String, JdbiWrapper> jdbiMap = new HashMap<>();

    /**
     * jdbc-jar 在pom已经依赖
     */
    public JdbiFactory() {
        this.dataSourceFactory = new DruidDataSourceFactoryImpl();
    }


    /**
     * jdbc-jar 未在pom依赖。需要动态加载配置的jdbc-jar文件
     * @param jarFilePath  jar 文件路径。
     */
    public JdbiFactory(Set<String> jarFilePath) throws Exception {
        this.dataSourceFactory = new DruidDataSourceFactoryImpl(jarFilePath);
    }


    /**
     * 创建jdbc
     * @param key jdbc 唯一的key
     * @param properties jdbc 的配置
     * @return jdbi
     */
    public synchronized Jdbi createJdbi(String key, Properties properties){

        JdbiWrapper jdbiWrapper = jdbiMap.get(key);
        try {
            if(jdbiWrapper != null){
                if(jdbiWrapper.jdbi != null){
                    return jdbiWrapper.jdbi;
                } else {
                    jdbiWrapper.jdbi = getJdbi(key, properties);
                    jdbiWrapper.key = key;
                    jdbiWrapper.properties = properties;
                    return jdbiWrapper.jdbi;
                }
            } else {
                jdbiWrapper = new JdbiWrapper();
                jdbiWrapper.jdbi = getJdbi(key, properties);
                jdbiWrapper.key = key;
                jdbiWrapper.properties = properties;
                jdbiMap.put(key, jdbiWrapper);
                return jdbiWrapper.jdbi;
            }
        } catch (Exception e) {
            log.error("create jdbi {} error.{}", key, e.getMessage(), e);
            return null;
        }
    }




    /**
     * 得到Jdbi
     * @param key Jdbi的key
     * @return jdbi
     */
    public synchronized Jdbi getJdbi(String key){
        JdbiWrapper jdbiWrapper = jdbiMap.get(key);
        if(jdbiWrapper == null){
            log.error("Not found {} jdbi. jdbi wrapper is null", key);
            return null;
        }
        if(jdbiWrapper.jdbi != null){
            return jdbiWrapper.jdbi;
        }
        // jdbiWrapper.jdbi 为 null.则就创建jdbi
        if(jdbiWrapper.properties != null){
            try {
                jdbiWrapper.jdbi = getJdbi(key, jdbiWrapper.properties);
                jdbiWrapper.key = key;
                return jdbiWrapper.jdbi;
            } catch (Exception e) {
                log.error("get jdbi {} error.{}", key, e.getMessage(), e);
            }
        }
        log.error("Not found {} jdbi. jdbi properties null", key);
        return null;
    }

    /**
     * 移除jdbi
     * @param key jdbi 唯一的key
     */
    public synchronized void removeJdbc(String key){
        JdbiWrapper jdbiWrapper = jdbiMap.get(key);
        if(jdbiWrapper != null){
            jdbiWrapper.jdbi = null;
            dataSourceFactory.close(key);
        }
    }


    /**
     * 销毁操作
     */
    public synchronized void destroy(){
        jdbiMap.clear();
        dataSourceFactory.closeAll();
    }


    /**
     * 得到jdbi
     * @param key jdbi key
     * @param properties 配置文件
     * @return Jdbi
     * @throws Exception 异常信息
     */
    private Jdbi getJdbi(String key, Properties properties) throws Exception {
        DataSource dataSource = dataSourceFactory.create(key, properties);
        return Jdbi.create(dataSource);
    }

    private class JdbiWrapper{

        private String key;
        private Jdbi jdbi;
        private Properties properties;

    }


}
