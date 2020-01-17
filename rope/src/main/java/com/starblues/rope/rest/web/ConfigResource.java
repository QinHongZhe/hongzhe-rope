package com.starblues.rope.rest.web;

import com.google.gson.Gson;
import com.starblues.rope.config.configuration.WebConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * config.js 配置文件
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/config.js")
public class ConfigResource {

    private final String config;

    @Autowired
    public ConfigResource(WebConfiguration webConfiguration,
                          Gson gson) {
        String json = gson.toJson(webConfiguration.getConfig());
        this.config =  "window.config = " + json;
    }


    @GetMapping()
    public String getConfig() {
        return this.config;
    }


}
