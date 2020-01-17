package com.starblues.rope.system.security.realms;

import com.starblues.rope.repository.entity.User;
import com.starblues.rope.service.user.UserService;
import com.starblues.rope.system.security.SecurityContext;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 系统授权认证的realm
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class AuthRealm extends AuthorizingRealm {

    private final UserService userService;

    public AuthRealm(UserService userService){
        this.userService = userService;
        setAuthenticationTokenClass(UsernamePasswordToken.class);
        setCredentialsMatcher(userService.getCredentialsMatcher());
        setName("AuthRealm");
    }



    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        User user = userService.getUser(usernamePasswordToken.getUsername());
        if(user == null){
            return null;
        }
        if(StringUtils.isEmpty(user.getPassword())){
            throw new IncorrectCredentialsException("User[" + user.getUsername() + "] password is empty");
        }
        User bingUser = new User();
        BeanUtils.copyProperties(user, bingUser);
        SecurityContext.bindUser(bingUser);
        return new SimpleAccount(user,
                user.getPassword(),
                ByteSource.Util.bytes(user.getSalt()),
                getName());
    }



}
