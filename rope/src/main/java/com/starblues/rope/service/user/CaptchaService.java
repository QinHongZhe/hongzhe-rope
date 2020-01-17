package com.starblues.rope.service.user;


import com.starblues.rope.rest.common.enums.CaptchaEnum;

/**
 * 验证码服务接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface CaptchaService {

    /**
     * 校验验证码(不删除缓存中)
     * @param inputCaptcha 客户端输入的验证码
     * @return 校验枚举结果
     */
    CaptchaEnum check(String inputCaptcha);


    /**
     * 生成并响应客户端验证
     * @throws Exception 生成验证码异常
     */
     void generate() throws Exception;


}
