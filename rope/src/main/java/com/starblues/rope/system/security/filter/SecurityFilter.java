package com.starblues.rope.system.security.filter;

import com.google.gson.Gson;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.rest.common.enums.LoginEnum;
import com.starblues.rope.system.security.realms.token.SessionToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 安全过滤器
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class SecurityFilter extends AccessControlFilter {

    private static final String TOKEN_BASIC = "Basic";
    private static final String TOKEN_BEARER = "Bearer";
    private final Gson gson;

    public SecurityFilter(Gson gson) {
        this.gson = gson;
    }

    private enum Type{
        /**
         * basic 认证
         */
        TOKEN_BASIC,

        /**
         * bearer 认证
         */
        TOKEN_BEARER
    }


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        AuthenticationToken authenticationToken = null;
        String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        Type type = null;
        if(!StringUtils.isEmpty(authorization)){
            if (authorization.startsWith(TOKEN_BASIC)) {
                final String base64UsernamePassword = authorization.substring(authorization.indexOf(' ') + 1);
                final String usernamePassword = decodeBase64(base64UsernamePassword);
                final String[] split = usernamePassword.split(":");

                if (split.length != 2) {
                    return unauthorized(response, LoginEnum.PARAMETER_ERROR,
                            "Invalid credentials in Authorization header");
                }
                authenticationToken = new UsernamePasswordToken(split[0], split[1]);
                type = Type.TOKEN_BASIC;
            } else if(authorization.startsWith(TOKEN_BEARER)){
                final String token = authorization.substring(authorization.indexOf(' ') + 1);
                authenticationToken = new SessionToken(
                        request.getRemoteHost(),
                        token
                );
                type = Type.TOKEN_BEARER;
            }
        }
        if(authenticationToken == null){
            return unauthorized(response, LoginEnum.ILLEGAL_ACCESS, null);
        }
        LoginEnum loginEnum = null;
        String errorMsg = null;
        try {
            getSubject(request, response).login(authenticationToken);
            return true;
        } catch (UnknownAccountException e){
            // 不存在的账户
            log.warn("Unauthorized : unknown account : {}", e.getMessage());
            loginEnum = LoginEnum.INCORRECT_CREDENTIALS;
        } catch (IncorrectCredentialsException e){
            // 密码错误
            log.warn("Unauthorized : credentials error : {}", e.getMessage());
            loginEnum = LoginEnum.INCORRECT_CREDENTIALS;
        } catch (LockedAccountException e){
            // 被锁定
            log.warn("Unauthorized : locked account : {}", e.getMessage());
            loginEnum = LoginEnum.LOCKED_ACCOUNT;
        } catch (DisabledAccountException e) {
            // 被禁用
            log.warn("Unauthorized : disabled account : {}", e.getMessage());
            loginEnum = LoginEnum.DISABLED_ACCOUNT;
        } catch (AuthenticationException e) {
            // 授权异常
            log.warn("Unauthorized : authentication error : {}", e.getMessage());
            loginEnum = LoginEnum.AUTHENTICATION_ERROR;
        } catch (Exception e){
            // 其他异常
            e.printStackTrace();
            log.warn("Unauthorized : other exception : {}", e.getMessage());
            loginEnum = LoginEnum.UNKNOWN_ACCOUNT;
        }
        if(type == Type.TOKEN_BASIC){
            return unauthorized(response, loginEnum, errorMsg);
        } else {
            return unauthorized(response, LoginEnum.ILLEGAL_ACCESS, null);
        }
    }


    private boolean unauthorized(ServletResponse response,
                                 LoginEnum loginEnum,
                                 String errorMsg){
        responseError(response,
                HttpServletResponse.SC_UNAUTHORIZED,
                loginEnum,
                errorMsg);
        return false;
    }


    private String decodeBase64(String s) {
        try {
            return new String(Base64.getDecoder().decode(s), StandardCharsets.US_ASCII);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }




    private void responseError(ServletResponse response,
                               int status,
                               LoginEnum loginEnum,
                               String errorMsg) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(status);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        PrintWriter out = null;
        try {
            Result result = Result.toBuilder()
                    .responseEnum(loginEnum)
                    .msg(StringUtils.isEmpty(errorMsg)?loginEnum.getMessage():errorMsg)
                    .builder();
            String json = gson.toJson(result);
            out = httpServletResponse.getWriter();
            out.append(json);
        } catch (IOException e) {
            e.printStackTrace();
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


}
