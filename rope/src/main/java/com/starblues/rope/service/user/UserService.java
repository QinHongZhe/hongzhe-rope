package com.starblues.rope.service.user;

import com.starblues.rope.repository.entity.User;
import com.starblues.rope.service.user.model.FrontUserInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface UserService {


    /**
     * 通过用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUser(String username);

    /**
     * 保存用户
     * @param user 用户信息
     * @return 成功true. 失败false
     */
    boolean save(User user);


    /**
     * 得到密码匹配者
     * @return CredentialsMatcher
     */
    CredentialsMatcher getCredentialsMatcher();



    /**
     * 获取当前访问的用户.只有在用户认证后可以获取到
     * @return User
     */
    User getAuthUser();

    /**
     * 返回前端的用户信息
     * @return UserInfo
     */
    FrontUserInfo getFrontUserInfo();

}
