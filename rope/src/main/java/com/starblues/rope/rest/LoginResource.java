package com.starblues.rope.rest;

import com.starblues.rope.rest.common.BaseResource;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.rest.common.enums.LoginEnum;
import com.starblues.rope.service.user.SessionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("login")
@Slf4j
public class LoginResource extends BaseResource {

    private final SessionService sessionService;

    public LoginResource(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping()
    public Result<String> login(@RequestBody @Valid LoginInfo loginInfo) {
        try {
            return sessionService.login(
                    loginInfo.getUsername(),
                    loginInfo.getPassword(),
                    loginInfo.getCode());
        } catch (Exception e) {
            if(log.isDebugEnabled()){
                e.printStackTrace();
            }
            log.error("Login failure : {}", e.getMessage());
            return responseBody(LoginEnum.AUTHENTICATION_ERROR, e.getMessage());
        }
    }

    @Data
    private static class LoginInfo{

        @NotEmpty(message = "用户名不能为空")
        private String username;

        @NotEmpty(message = "密码不能为空")
        private String password;


        private String code;
    }



}
