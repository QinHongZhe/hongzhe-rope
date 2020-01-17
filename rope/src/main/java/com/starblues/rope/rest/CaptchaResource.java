package com.starblues.rope.rest;

import com.starblues.rope.service.user.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取验证码
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("captcha")
@Slf4j
public class CaptchaResource {

    private final CaptchaService captchaService;

    @Autowired
    public CaptchaResource(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping
    public void captcha() throws Exception{
        captchaService.generate();
    }

}
