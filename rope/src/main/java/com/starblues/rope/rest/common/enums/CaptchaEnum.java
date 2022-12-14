package com.starblues.rope.rest.common.enums;


import com.starblues.rope.rest.common.ResponseEnum;

/**
 * 验证码校验结果枚举
 *
 * @author zhangzhuo
 * @version 1.0
 */
public enum CaptchaEnum implements ResponseEnum {

    /**
     * 成功
     */
    SUCCESS(1, "success"),

    /**
     * 验证码错误
     */
    ERROR(2, "验证码错误"),

    /**
     * 验证码过期
     */
    OVERDUE(3, "验证码过期"),

    /**
     * 成功
     */
    EMPTY(4, "输入的验证码为空");


    private Integer code;
    private String message;


    CaptchaEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }


}
