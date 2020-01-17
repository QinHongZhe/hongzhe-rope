package com.starblues.rope.config.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaConfiguration {
    /**
     * 登录时是否开启验证码校验
     */
    @Value("${enable:true}")
    private Boolean enable = false;

    /**
     * 验证码cookie的key
     */
    @Value("${cookieKey:captcha}")
    private String cookieKey = "captcha";

    /**
     * 过期时间。单位秒.默认300秒(3分钟)过期
     */
    @Value("${expire:300}")
    private Long expire = 300L;

    /**
     * 验证码配置。详见: https://www.cnblogs.com/louis80/p/5230507.html
     */
    private Map<String, String> config = new HashMap<>();
}
