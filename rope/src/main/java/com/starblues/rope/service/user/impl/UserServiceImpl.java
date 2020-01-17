package com.starblues.rope.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Sets;
import com.starblues.rope.repository.entity.User;
import com.starblues.rope.repository.service.UserDao;
import com.starblues.rope.service.user.UserService;
import com.starblues.rope.service.user.model.FrontUserInfo;
import com.starblues.rope.system.security.SecurityContext;
import com.starblues.rope.utils.IDUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class UserServiceImpl implements UserService {


    private final static int HASH_ITERATIONS = 2;
    private final UserDao userDao;
    private final CredentialsMatcher hashedCredentialsMatcher;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Md5Hash.ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(HASH_ITERATIONS);
        this.hashedCredentialsMatcher = hashedCredentialsMatcher;
    }

    @Override
    public User getUser(String username) {
        Wrapper<User> wrapper = Wrappers.<User>query()
                .eq("username", username);
        return userDao.getOne(wrapper);
    }

    @Override
    public boolean save(User user) {
        user.setHashIterations(2);
        user.setSalt(IDUtils.uuid());
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), user.getSalt(), HASH_ITERATIONS);
        user.setPassword(md5Hash.toHex());
        return userDao.save(user);
    }

    @Override
    public CredentialsMatcher getCredentialsMatcher() {
        return this.hashedCredentialsMatcher;
    }

    @Override
    public User getAuthUser() {
        return SecurityContext.getUser();
    }

    @Override
    public FrontUserInfo getFrontUserInfo() {
        User user = SecurityContext.getUser();
        FrontUserInfo.UserInfo userInfo = new FrontUserInfo.UserInfo();
        userInfo.setUsername(user.getUsername());
        userInfo.setName(user.getName());
        // TODO 确少用户头像
        FrontUserInfo frontUserInfo = new FrontUserInfo();
        frontUserInfo.setUserInfo(userInfo);
        frontUserInfo.setRoles("admin");
        // TODO set 该用户权限
        frontUserInfo.setPermission(Sets.newHashSet());
        return frontUserInfo;
    }
}
