package com.starblues.rope.service.user.impl;

import com.starblues.rope.repository.entity.User;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.rest.common.enums.CaptchaEnum;
import com.starblues.rope.rest.common.enums.LoginEnum;
import com.starblues.rope.service.user.CaptchaService;
import com.starblues.rope.service.user.SessionService;
import com.starblues.rope.service.user.UserService;
import com.starblues.rope.system.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * session 服务类
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final static String USERNAME = "username";

    private final JwtService jwtService;
    private final CaptchaService captchaService;
    private final UserService userService;

    public SessionServiceImpl(JwtService jwtService, CaptchaService captchaService,
                              UserService userService) {
        this.jwtService = jwtService;
        this.captchaService = captchaService;
        this.userService = userService;
    }


    @Override
    public Result<String> login(String username, String password, String captcha) throws Exception{
        CaptchaEnum captchaEnum = captchaService.check(captcha);
        if(captchaEnum != CaptchaEnum.SUCCESS){
            return Result.<String>toBuilder()
                    .responseEnum(captchaEnum)
                    .builder();
        }
        return login(username, password);
    }

    @Override
    public Result<String> login(String username, String password) throws Exception{
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        try {
            subject.login(usernamePasswordToken);

            Object principal = subject.getPrincipal();
            if(principal instanceof User){
                // 为该用户生成jwt token
                User user = (User) principal;
                String jwt = jwtService.encrypt(user.getTtlMillis(), claims -> {
                    claims.put(USERNAME, username);
                });
                return Result.<String>toBuilder()
                        .responseEnum(LoginEnum.SUCCESS)
                        .data(jwt)
                        .builder();
            } else {
                return Result.<String>toBuilder()
                        .responseEnum(LoginEnum.AUTHENTICATION_ERROR)
                        .builder();
            }
        } catch (UnknownAccountException e){
            // 不存在的账户
            log.warn("unknown account : {}", e.getMessage());
            return Result.<String>toBuilder()
                    .responseEnum(LoginEnum.UNKNOWN_ACCOUNT)
                    .builder();
        } catch (IncorrectCredentialsException e){
            // 密码错误
            log.warn("Unauthorized : credentials error : {}", e.getMessage());
            return Result.<String>toBuilder()
                    .responseEnum(LoginEnum.INCORRECT_CREDENTIALS)
                    .builder();
        } catch (LockedAccountException e){
            // 被锁定
            log.warn("Unauthorized : locked account : {}", e.getMessage());
            return Result.<String>toBuilder()
                    .responseEnum(LoginEnum.LOCKED_ACCOUNT)
                    .builder();
        } catch (DisabledAccountException e) {
            // 被禁用
            log.warn("Unauthorized : disabled account : {}", e.getMessage());
            return Result.<String>toBuilder()
                    .responseEnum(LoginEnum.DISABLED_ACCOUNT)
                    .builder();
        } catch (AuthenticationException e) {
            // 授权异常
            log.warn("Unauthorized : authentication error : {}", e.getMessage());
            return Result.<String>toBuilder()
                    .responseEnum(LoginEnum.AUTHENTICATION_ERROR)
                    .builder();
        } catch (Exception e){
            // 其他异常
            e.printStackTrace();
            log.warn("Unauthorized : other exception : {}", e.getMessage());
            return Result.<String>toBuilder()
                    .responseEnum(LoginEnum.UNKNOWN_ACCOUNT)
                    .builder();
        }
    }



    @Override
    public Result<User> login(String jwt) {
        try {
            Claims claims = jwtService.decode(jwt);
            String username = claims.get(USERNAME, String.class);
            if(!StringUtils.isEmpty(username)){
                User user = userService.getUser(username);
                if(user != null){
                    return Result.<User>toBuilder()
                            .responseEnum(LoginEnum.SUCCESS)
                            .data(user)
                            .builder();
                }
            }
            return Result.<User>toBuilder()
                    .responseEnum(LoginEnum.AUTHENTICATION_ERROR)
                    .builder();
        } catch (ExpiredJwtException e){
            // jwt 过期
            log.warn("Jwt '{}' expired. {}", jwt, e.getMessage());
            return Result.<User>toBuilder()
                    .responseEnum(LoginEnum.OVERDUE_ACCOUNT)
                    .msg("授权code已经过期")
                    .builder();
        } catch (Exception e) {
            // 非法jwt
            log.warn("Jwt '{}' Not authorized. {}", jwt, e.getMessage());
            return Result.<User>toBuilder()
                    .responseEnum(LoginEnum.AUTHENTICATION_ERROR)
                    .builder();
        }
    }

}
