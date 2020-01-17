package com.starblues.rope.common.databases.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * jar 包加载者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ChainingJarLoader implements JarLoader{

    private static final Logger LOG = LoggerFactory.getLogger(ChainingClassLoader.class);
    private ChainingClassLoader classLoader;

    public ChainingJarLoader() {
        this.classLoader = new ChainingClassLoader(ChainingJarLoader.class.getClassLoader());
    }


    @Override
    public void load(String path) throws Exception {
        try {
            URL driverUrl = new URL("file:"+path);
            final boolean result = classLoader.addClassLoader(URLClassLoader.newInstance(new URL[]{driverUrl}));
            if(!result){
                LOG.error("load {} failure from classLoader", path);
                throw new Exception("load " + path + "load {} failure from classLoader!");
            }
            LOG.info("load {} success", path);
        } catch (MalformedURLException | ClassNotFoundException e) {
            LOG.error("Cannot open JAR file for discovering jdbcDriver <{}>", path, e);
            throw new Exception("Can't find jdbc driver in " + path, e);
        }
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
