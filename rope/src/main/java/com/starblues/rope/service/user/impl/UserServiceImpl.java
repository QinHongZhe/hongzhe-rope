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
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户操作实现类
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
@Slf4j
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
        user.setPassword(getHashPassword(user.getPassword(), user.getSalt()));
        return userDao.save(user);
    }

    @Override
    public boolean updatePassword(String oldPassword, String newPassword) throws Exception{
        User authUser = getAuthUser();
        if(authUser == null){
            log.error("Update password failure. Auth error : current auth user is null");
            throw new Exception("授权失败!");
        }
        User user = userDao.getById(authUser.getUserId());
        if(user == null){
            log.error("Update password failure. Not found this user: {}", authUser.getUserId());
            throw new Exception("修改密码失败, 没有发现该用户!");
        }
        String hashPassword = getHashPassword(oldPassword, user.getSalt());
        if(Objects.equals(hashPassword, user.getPassword())){
            user.setPassword(getHashPassword(newPassword, user.getSalt()));
            return userDao.updateById(user);
        } else {
            throw new Exception("修改密码失败, 旧密码错误");
        }
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

    /**
     * 得到加密的密码
     * @param password 要加密的密码
     * @param slat 加盐
     * @return 加密的密码
     */
    private String getHashPassword(String password, String slat){
        Md5Hash md5Hash = new Md5Hash(password, slat, HASH_ITERATIONS);
        return md5Hash.toHex();
    }

}
