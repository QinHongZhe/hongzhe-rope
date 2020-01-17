package com.starblues.rope.utils;

import com.gitee.starblues.integration.user.PluginUser;
import com.google.gson.Gson;
import com.starblues.rope.core.common.param.ConfigParamInfo;
import com.starblues.rope.core.common.param.ConfigParameter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * 公用操作工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class CommonUtils {

    private CommonUtils(){

    }

    /**
     * 得到系统中满足条件的接口的实现类
     * @param pluginUser 插件使用者
     * @param interfaceClass 接口类
     * @param function 函数式判断
     * @param <T> 泛型
     * @return 得到实现类
     */
    public static <T> T getImpls(PluginUser pluginUser,
                                 Class<T> interfaceClass,
                                 Function<T, Boolean> function) {
        List<T> impls = pluginUser.getBeans(interfaceClass);
        if(impls == null || impls.isEmpty()){
            return null;
        }
        for (T impl : impls) {
            if(impl == null){
                continue;
            }
            if(function.apply(impl)){
                return impl;
            }
        }
        return null;
    }


    /**
     * 解析 ConfigParameter 配置参数
     * @param configParameter 配置解析者
     * @param params 参数Map集合
     * @throws Exception 解析异常
     */
    public static void parsingConfig(ConfigParameter configParameter,
                                     Map<String, Object> params) throws Exception{
        if(configParameter == null){
            return;
        }
        if(params == null || params.isEmpty()){
            return;
        }
        ConfigParamInfo configParamInfo = new ConfigParamInfo(params);
        configParameter.parsing(configParamInfo);
    }


    /**
     * 得到要初始化的参数
     * @param params 参数
     * @param paramClass 参数bean
     * @return 参数bean实例
     */
    public static Object getParam(Gson gson, Map<String, Object> params, Class paramClass){
        if(params == null){
            return null;
        }
        if(Converter.isBasicDataType(paramClass)){
            // String 类型
            Object first = getFirst(params);
            if(first == null){
                return null;
            }
            return Converter.cast(first, paramClass);
        }

        String json = gson.toJson(params);
        return gson.fromJson(json, paramClass);
    }

    /**
     * 得到泛型的类型的数组
     * @param aClass 抽象类
     * @return 类型数组
     */
    public static Type[] getParameterizedType(Class aClass){
        if(aClass == null){
            return null;
        }
        Type type = aClass.getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        return params;
    }



    /**
     * 生成uuid
     * @return uuid
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 得到第一个参数的值
     * @param params 参数
     * @return 第一个参数的值
     */
    private static Object getFirst(Map<String, Object> params){
        if(params == null){
            return null;
        }
        for (String key : params.keySet()) {
            return params.get(key);
        }
        return null;
    }



}
