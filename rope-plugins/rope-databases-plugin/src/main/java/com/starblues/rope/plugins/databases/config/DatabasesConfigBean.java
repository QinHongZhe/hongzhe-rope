package com.starblues.rope.plugins.databases.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.starblues.rope.common.databases.JdbiFactory;
import com.gitee.starblues.realize.ConfigBean;
import com.google.common.collect.Sets;
import com.starblues.rope.utils.ParamUtils;
import org.jdbi.v3.core.Jdbi;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 数据库配置bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DatabasesConfigBean implements ConfigBean {

    private JdbiFactory jdbiFactory;


    private final DatabasesConfig databasesConfig;

    public DatabasesConfigBean(DatabasesConfig databasesConfig) {
        this.databasesConfig = databasesConfig;
    }


    @Override
    public void initialize() throws Exception {
        List<DatabaseConfig> databases = databasesConfig.getDatabases();
        if(databases == null || databases.isEmpty()){
            return;
        }
        Set<String> jarPaths = Sets.newHashSet();
        for (DatabaseConfig database : databases) {
            jarPaths.add(database.getJarPath());
        }
        jdbiFactory = new JdbiFactory(jarPaths);
        for (DatabaseConfig database : databases) {
            Properties properties = database.getConfig();
            Object name = properties.get(DruidDataSourceFactory.PROP_NAME);
            if(StringUtils.isEmpty(name)){
                properties.setProperty(DruidDataSourceFactory.PROP_NAME, database.getName());
            }
            jdbiFactory.createJdbi(database.getKey(), database.getConfig());
        }
    }

    @Override
    public void destroy() throws Exception {
        jdbiFactory.destroy();
    }

    /**
     * 销毁
     * @param key  databaseKey
     */
    public void destroy(String key){
        ParamUtils.check("DatabaseKey", key);
        jdbiFactory.removeJdbc(key);
    }

    /**
     * 得到 Jdbi
     * @param key databaseKey
     * @return Jdbi
     */
    public Jdbi getJdbi(String key){
        ParamUtils.check("DatabaseKey", key);
        Jdbi jdbc = jdbiFactory.getJdbi(key);
        if(jdbc == null){
            throw new RuntimeException("Not found database key : " + key);
        }
        return jdbc;
    }

}
