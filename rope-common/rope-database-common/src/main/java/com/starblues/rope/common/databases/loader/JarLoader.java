package com.starblues.rope.common.databases.loader;

/**
 * 数据库连接池工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface JarLoader {

    /**
     * 加载
     * @param path jar 路径
     * @throws Exception 加载异常
     */
    void load(String path) throws Exception;


    /**
     * 加载class
     * @param className 类的包名
     * @return 类
     * @throws ClassNotFoundException 没有发现类的异常
     */
    Class<?> loadClass(String className) throws ClassNotFoundException;

    /**
     * 得到 ClassLoader
     * @return ClassLoader
     */
    ClassLoader getClassLoader();
}
