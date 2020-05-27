package com.starblues.rope.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 插件日志工厂
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-05-27
 */
public class PluginLogger {

    private PluginLogger(){}


    public static Logger getLogger(Class<?> aClass, String processId){
        return LoggerFactory.getLogger(aClass.getName() + "[" + processId + "]");
    }

    public static Logger getLogger(Object object, String processId){
        return getLogger(object.getClass(), processId);
    }


}
