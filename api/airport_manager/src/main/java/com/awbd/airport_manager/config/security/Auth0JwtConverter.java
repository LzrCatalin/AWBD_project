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

    @Value("${auth0.email-claim}")
    private String emailClaim;

    @Value("${auth0.name-claim}")
    private String nameClaim;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> rawRoles = jwt.getClaimAsStringList(rolesNamespace);
        Collection<GrantedAuthority> authorities = toAuthorities(rawRoles);

        UserInfo userInfo = new UserInfo(
                jwt.getSubject(),
                firstNonBlank(jwt.getClaimAsString(emailClaim), jwt.getClaimAsString("email")),
                firstNonBlank(jwt.getClaimAsString(nameClaim), jwt.getClaimAsString("name")),
                rawRoles,
                authorities
        );

        return new UserInfoAuthenticationToken(userInfo, jwt);
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) return primary;
        return fallback;
    }

    private Collection<GrantedAuthority> toAuthorities(List<String> roles) {
        if (roles == null) return List.of();
        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
