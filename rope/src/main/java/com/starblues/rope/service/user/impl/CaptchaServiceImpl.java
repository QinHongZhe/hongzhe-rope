package com.starblues.rope.service.user.impl;

import com.google.code.kaptcha.Producer;
import com.starblues.rope.config.configuration.CaptchaConfiguration;
import com.starblues.rope.system.security.SecurityContext;
import com.starblues.rope.rest.common.enums.CaptchaEnum;
import com.starblues.rope.service.user.CaptchaService;
import com.starblues.rope.system.security.jwt.JwtService;
import com.starblues.rope.utils.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * 验证码服务类
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaConfiguration configuration;
    private final Producer captchaProducer;
    private final JwtService jwtService;

    public CaptchaServiceImpl(CaptchaConfiguration captchaConfiguration,
                              Producer producer,
                              JwtService jwtService) {
        this.configuration = captchaConfiguration;
        this.captchaProducer = producer;
        this.jwtService = jwtService;
    }


    @Override
    public CaptchaEnum check(String inputCaptcha) {
        if(!configuration.getEnable()){
            return CaptchaEnum.SUCCESS;
        }
        if(StringUtils.isEmpty(inputCaptcha)){
            return CaptchaEnum.EMPTY;
        }
        String cookieValue = CookieUtils.getCookieValue(
                SecurityContext.getRequest(), configuration.getCookieKey(), true);
        try {
            if(StringUtils.isEmpty(cookieValue)){
                return CaptchaEnum.ERROR;
            }
            Claims claims = jwtService.decode(cookieValue);
            String capText = claims.get(configuration.getCookieKey(), String.class);
            if(Objects.equals(inputCaptcha, capText)){
                return CaptchaEnum.SUCCESS;
            } else {
                return CaptchaEnum.ERROR;
            }
        } catch (ExpiredJwtException e){
            if(log.isDebugEnabled()){
                e.printStackTrace();
            }
            log.warn("Captcha overdue. {}", e.getMessage());
            return CaptchaEnum.OVERDUE;
        } catch (Exception e) {
            if(log.isDebugEnabled()){
                e.printStackTrace();
            }
            log.warn("Captcha check error. {}", e.getMessage());
            return CaptchaEnum.ERROR;
        }
    }

    @Override
    public void generate() throws Exception {
        HttpServletRequest request = SecurityContext.getRequest();
        HttpServletResponse response = SecurityContext.getResponse();
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText = captchaProducer.createText();
        String encryptCookieValue = null;

        String cookieValue = CookieUtils.getCookieValue(request, configuration.getCookieKey(),
                true);
        Long expire = configuration.getExpire();
        if(expire == null || expire <= 0){
            expire = 300L * 1000L;
        } else {
            expire = expire * 1000L;
        }
        encryptCookieValue = jwtService.encrypt(expire , claims -> {
            claims.put(configuration.getCookieKey(), capText);
        });
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            CookieUtils.setCookie(SecurityContext.getResponse(),
                    configuration.getCookieKey(), encryptCookieValue, configuration.getExpire().intValue(),
                    true);
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }
        }
    }

}
