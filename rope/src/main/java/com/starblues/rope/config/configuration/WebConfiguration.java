package com.starblues.rope.config.configuration;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * web 配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "web")
public class WebConfiguration {


    private Map<String, Object> config = Maps.newHashMap();

}
