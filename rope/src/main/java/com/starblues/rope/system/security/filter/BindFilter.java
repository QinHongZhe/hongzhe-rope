package com.starblues.rope.system.security.filter;

import com.starblues.rope.system.security.SecurityContext;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 绑定 HttpServletRequest、HttpServletResponse
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class BindFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        SecurityContext.bindRequest(httpServletRequest);
        SecurityContext.bindResponse(httpServletResponse);
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
       return true;
    }
}
