package com.starblues.rope.service.user;

import com.starblues.rope.repository.entity.User;
import com.starblues.rope.rest.common.Result;

/**
 * session 服务
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface SessionService {

    /**
     * 登录(含验证码)
     * @param username 用户名
     * @param password 密码
     * @param captcha 验证码
     * @return 登录后的token
     * @throws Exception 登录异常
     */
    Result<String> login(String username, String password, String captcha) throws Exception;

    /**
     * 登录(不含验证码)
     * @param username 用户名
     * @param password 密码
     * @return 登录后的token
     * @throws Exception 登录异常
     */
    Result<String> login(String username, String password) throws Exception;



    /**
     * jwt 登录
     * @param jwt 使用jwt登录
     * @return 成功返回true. 失败返回false
     */
    Result<User> login(String jwt);


}
