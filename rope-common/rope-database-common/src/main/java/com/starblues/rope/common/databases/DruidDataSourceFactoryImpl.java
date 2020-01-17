package com.starblues.rope.common.databases;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.StringUtils;
import com.starblues.rope.common.databases.loader.ChainingJarLoader;
import com.starblues.rope.common.databases.loader.JarLoader;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Druid 数据库连接池
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DruidDataSourceFactoryImpl implements DataSourceFactory{

    private final Map<String, DataSourceBean> dataSourceMap;
    private final Lock lock = new ReentrantLock(true);

    private final JarLoader jarLoader = new ChainingJarLoader();


    public DruidDataSourceFactoryImpl(){
        this.dataSourceMap = new HashMap<String, DataSourceBean>();
    }

    public DruidDataSourceFactoryImpl(Set<String> jarFilePath) throws Exception {
        this.dataSourceMap = new HashMap<String, DataSourceBean>();
        if(jarFilePath != null){
            for (String pathString : jarFilePath) {
                if(StringUtils.isEmpty(pathString)){
                    continue;
                }
                Path path = Paths.get(pathString);
                if(Files.exists(path)){
                    jarLoader.load(pathString);
                }
            }
        }
    }

    @Override
    public DataSource create(String key, Properties properties) throws Exception {
        lock.lock();
        try {
            Object urlObject = properties.get(DruidDataSourceFactory.PROP_URL);
            if(urlObject == null){
                throw new NullPointerException("url can't be null");
            }
            Object driverClassNameObject = properties.get(DruidDataSourceFactory.PROP_DRIVERCLASSNAME);
            if(driverClassNameObject == null){
                throw new NullPointerException("driverClassName can't be null");
            }
            if(dataSourceMap.containsKey(key)){
                DataSourceBean dataSourceBean = dataSourceMap.get(key);
                dataSourceBean.referenceCount++;
                return dataSourceBean.dataSource;
            }
            DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
            druidDataSource.setDriverClassLoader(jarLoader.getClassLoader());
            DataSourceBean dataSourceBean = new DataSourceBean(druidDataSource);
            dataSourceMap.put(key, dataSourceBean);
            return druidDataSource;
        }  finally {
            lock.unlock();
        }
    }

    @Override
    public void close(String key) {
        lock.lock();
        try {
            DataSourceBean dataSourceBean = dataSourceMap.get(key);
            if(dataSourceBean != null && dataSourceBean.referenceCount-- == 0){
                close(dataSourceBean.dataSource);
                dataSourceMap.remove(key);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void closeAll() {
        lock.lock();
        try {
            for (DataSourceBean dataSourceBean : dataSourceMap.values()) {
                close(dataSourceBean.dataSource);
            }
            dataSourceMap.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭 DataSource
     * @param dataSource dataSource
     */
    private void close(DruidDataSource dataSource){
        if(dataSource != null){
            dataSource.close();
        }
    }


    private class DataSourceBean{
        private DruidDataSource dataSource;
        private int referenceCount = 1;

        public DataSourceBean(DruidDataSource dataSource) {
            this.dataSource = dataSource;
        }
    }


}
