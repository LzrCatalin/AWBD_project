package com.awbd.airport_manager.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class Auth0JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${auth0.roles-namespace}")
    private String rolesNamespace;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> rawRoles = jwt.getClaimAsStringList(rolesNamespace);
        Collection<GrantedAuthority> authorities = toAuthorities(rawRoles);

        UserInfo userInfo = new UserInfo(
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                rawRoles,
                authorities
        );

        return new UserInfoAuthenticationToken(userInfo, jwt);
    }

    private Collection<GrantedAuthority> toAuthorities(List<String> roles) {
        if (roles == null) return List.of();
        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
