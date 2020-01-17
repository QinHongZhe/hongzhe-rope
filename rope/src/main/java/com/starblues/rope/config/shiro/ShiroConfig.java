package com.starblues.rope.config.shiro;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starblues.rope.system.security.filter.BindFilter;
import com.starblues.rope.system.security.filter.SecurityFilter;
import com.starblues.rope.system.security.realms.AuthRealm;
import com.starblues.rope.system.security.realms.SessionAuthRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.Map;

/**
 * shiro 的配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Configuration
@Slf4j
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(AuthRealm authRealm,
                                           SessionAuthRealm sessionAuthRealm){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        MultiRealmAuthenticator multiRealmAuthenticator = new MultiRealmAuthenticator();
        multiRealmAuthenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        securityManager.setAuthenticator(multiRealmAuthenticator);

        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        securityManager.setRealms(Lists.newArrayList(
                authRealm,
                sessionAuthRealm
        ));
        securityManager.setRememberMeManager(new CookieRememberMeManager());
        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager,
                                             BindFilter bindFilter,
                                             SecurityFilter securityFilter){
        ShiroFilterFactoryBean shiroFilterFactoryBean  = new ShiroFilterFactoryBean();
        Map<String, Filter> filtersMap = Maps.newLinkedHashMap();
        filtersMap.put("bindFilter", bindFilter);
        filtersMap.put("securityFilter", securityFilter);
        shiroFilterFactoryBean.setFilters(filtersMap);
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //拦截器.
        Map<String,String> filterChainDefinitionMap = Maps.newLinkedHashMap();
        filterChainDefinitionMap.put("/**", "bindFilter");
        filterChainDefinitionMap.put("/api/**", "securityFilter");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }


    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
