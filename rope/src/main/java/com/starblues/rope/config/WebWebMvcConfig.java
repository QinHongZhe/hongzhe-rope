package com.starblues.rope.config;

import com.starblues.rope.system.security.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

/**
 * web 配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Configuration
public class WebWebMvcConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 前端界面配置
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:/rope-web/");

        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noStore());
    }


    /**
     * 跳转到前端首页
     * @param registry registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/admin").setViewName("forward:/admin/index.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }



    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        // 新建过滤器注册类
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
        registration.setFilter(new CorsFilter());
        registration.addUrlPatterns("/*");
        //设置过滤器顺序
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }


}
