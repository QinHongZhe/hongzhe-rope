package com.starblues.rope.utils;

import org.springframework.util.StringUtils;

/**
 * 参数工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ParamUtils {

    private ParamUtils(){}

    /**
     * 检查参数
     * @param key 参数的标识符。用于异常打印
     * @param value 参数的值
     * @param <T> 参数的类型
     * @return 参数的值
     */
    public static <T> T check(String key, T value){
        if(StringUtils.isEmpty(value)){
            String error = TextUtils.format("param '{}' can't be empty", key);
            throw new IllegalArgumentException(error);
        }
        return value;
    }


}
