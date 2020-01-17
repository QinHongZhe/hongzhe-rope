package com.starblues.rope.system.security;

import com.starblues.rope.repository.entity.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SecurityContext
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class SecurityContext {


    private static final String REQUEST_KEY = SecurityContext.class.getName() + "_HttpServletRequest";
    private static final String RESPONSE_KEY = SecurityContext.class.getName() + "_HttpServletResponse";

    private static final String USER_KEY = SecurityContext.class.getName() + "_User";


    private Subject subject;
    private final AuthenticationToken token;


    public SecurityContext(Subject subject,
                           AuthenticationToken token) {
        this.subject = subject;
        this.token = token;
    }


    public String getUsername() {
        if (token == null || token.getPrincipal() == null) {
            return null;
        }
        return token.getPrincipal().toString();
    }

    public String getPassword() {
        if (token == null || token.getCredentials() == null) {
            return null;
        }
        return token.getCredentials().toString();
    }

    public void loginSubject() throws AuthenticationException {
        subject.login(token);
    }


    public boolean isAuthenticated(){
        return subject.isAuthenticated();
    }

    /**
     * 绑定数据
     * @param key 绑定的ky
     * @param value 绑定的值
     */
    public static void bind(String key, Object value){
        ThreadContext.put(key, value);
    }

    /**
     * 获取到所绑定的数据
     * @param key 绑定的key
     * @param <T> 绑定的类型
     * @return 绑定的数据
     */
    public static <T> T getBind(String key){
        Object o = ThreadContext.get(key);
        if(o == null){
            return null;
        }
        final T t = (T) o;
        return t;
    }

    /**
     * 绑定当前请求线程的 httpServletRequest
     * @param httpServletRequest httpServletRequest
     */
    public static void bindRequest(HttpServletRequest httpServletRequest){
        bind(REQUEST_KEY, httpServletRequest);
    }

    /**
     * 绑定当前请求线程的 httpServletResponse
     * @param httpServletResponse httpServletResponse
     */
    public static void bindResponse(HttpServletResponse httpServletResponse){
        bind(RESPONSE_KEY, httpServletResponse);
    }

    /**
     * 绑定当前登录的用户
     * @param user 用户信息
     */
    public static void bindUser(User user){
        user.setPassword(null);
        bind(USER_KEY, user);
    }



    /**
     * 获取到当前请求线程的 httpServletRequest
     * @return httpServletRequest
     */
    public static HttpServletRequest getRequest(){
        return getBind(REQUEST_KEY);
    }

    /***
     * 获取到当前请求线程的 HttpServletResponse
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse(){
        return getBind(RESPONSE_KEY);
    }


    /**
     * 绑定当前登录的用户
     */
    public static User getUser(){
        return getBind(USER_KEY);
    }

}
