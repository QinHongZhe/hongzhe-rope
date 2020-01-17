package com.starblues.rope.system.security.filter;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class CorsFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 跨域处理
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String origin = httpServletRequest.getHeader("Origin");
        if(!StringUtils.isEmpty(origin)) {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", origin);
        }

        // 自适应所有自定义头
        String headers = httpServletRequest.getHeader("Access-Control-Request-Headers");
        if(!StringUtils.isEmpty(headers)) {
            httpServletResponse.setHeader("Access-Control-Allow-Headers", headers);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", headers);
        }

        // 允许跨域的请求方法类型
        httpServletResponse.setHeader("Access-Control-Allow-Methods",
                "GET, POST, DELETE, PUT, OPTIONS, HEAD");
        // 预检命令（OPTIONS）缓存时间，单位：秒
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equalsIgnoreCase(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
