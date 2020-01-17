package com.starblues.rope.config;

import com.gitee.starblues.integration.application.PluginApplication;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starblues.rope.config.configuration.CaptchaConfiguration;
import com.starblues.rope.process.factory.DefaultProcessFactory;
import com.starblues.rope.process.factory.ProcessFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

/**
 * 公用的bean配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Configuration
public class CommonBeanConfig {

    @Bean
    public Gson gson(){
        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting().create();
    }


    @Bean
    public ProcessFactory processFactory(PluginApplication pluginApplication){
        return new DefaultProcessFactory(pluginApplication);
    }


    /**
     * 验证码的配置
     * @return Producer
     */
    @Bean
    public Producer captcha(CaptchaConfiguration captchaConfiguration){
        Map<String, String> configMap = captchaConfiguration.getConfig();
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 设置默认配置
        properties.put("kaptcha.border", "yes");
        properties.put("kaptcha.border.color", "105,179,90");
        properties.put("kaptcha.textproducer.font.color", "blue");
        properties.put("kaptcha.image.width", "125");
        properties.put("kaptcha.image.height", "45");
        properties.put("kaptcha.textproducer.font.size", "45");
        properties.put("kaptcha.textproducer.char.length", 4);
        properties.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        if(configMap != null){
            // 设置自定义配置
            configMap.forEach((k,v)->{
                properties.put(k, v);
            });
        }
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}
