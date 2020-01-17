package com.starblues.rope.utils;

import java.util.UUID;


/**
 * id 工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class IDUtils {


    private IDUtils(){}

    /**
     * 生成uuid
     * @return String
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
