package com.awbd.airport_manager.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public class UserInfoAuthenticationToken extends AbstractAuthenticationToken {

    private final UserInfo userInfo;
    private final Jwt jwt;

    public UserInfoAuthenticationToken(UserInfo userInfo, Jwt jwt) {
        super(userInfo.getAuthorities());
        this.userInfo = userInfo;
        this.jwt = jwt;
        setAuthenticated(true);
    }

    @Override
    public UserInfo getPrincipal() {
        return userInfo;
    }

    @Override
    public Jwt getCredentials() {
        return jwt;
    }
}
