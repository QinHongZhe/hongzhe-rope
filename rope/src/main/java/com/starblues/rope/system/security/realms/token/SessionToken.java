package com.starblues.rope.system.security.realms.token;

import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class SessionToken implements HostAuthenticationToken {

    private final String host;
    private final String token;

    public SessionToken(String host, String token) {
        this.host = host;
        this.token = token;
    }


    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

}
