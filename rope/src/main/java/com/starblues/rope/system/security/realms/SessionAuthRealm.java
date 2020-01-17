package com.starblues.rope.system.security.realms;

import com.starblues.rope.repository.entity.User;
import com.starblues.rope.rest.common.Result;
import com.starblues.rope.rest.common.enums.LoginEnum;
import com.starblues.rope.service.user.SessionService;
import com.starblues.rope.system.security.SecurityContext;
import com.starblues.rope.system.security.realms.token.SessionToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * jwt认证校验
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class SessionAuthRealm extends AuthorizingRealm {

    private final SessionService sessionService;


    public SessionAuthRealm(SessionService sessionService){
        this.sessionService = sessionService;
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
        setAuthenticationTokenClass(SessionToken.class);
        setName("SessionAuthRealm");
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SessionToken jwtToken = (SessionToken) token;
        String jwt = String.valueOf(jwtToken.getPrincipal());
        Result<User> loginResult = sessionService.login(jwt);
        if(Objects.equals(loginResult.getCode(), LoginEnum.SUCCESS.getCode())){
            SecurityContext.bindUser(loginResult.getData());
            return new SimpleAccount(jwt,
                    "",
                    getName());
        } else {
            return null;
        }
    }
}
