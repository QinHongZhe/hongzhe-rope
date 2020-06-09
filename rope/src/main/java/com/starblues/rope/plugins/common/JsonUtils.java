package com.starblues.rope.plugins.common;

import com.google.gson.Gson;

/**
 * json 工具
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-06-03
 */
public class JsonUtils {


    private final static Gson GSON = new Gson();


    private JsonUtils(){}


    public static Gson gson(){
        return GSON;
    }

}
