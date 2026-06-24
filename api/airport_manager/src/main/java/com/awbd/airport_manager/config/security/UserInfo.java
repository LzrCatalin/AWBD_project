package com.awbd.airport_manager.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class UserInfo {

    private final String sub;
    private final String email;
    private final String name;
    private final List<String> roles;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserInfo(String sub, String email, String name, List<String> roles,
                    Collection<? extends GrantedAuthority> authorities) {
        this.sub = sub;
        this.email = email;
        this.name = name;
        this.roles = roles != null ? roles : List.of();
        this.authorities = authorities != null ? authorities : List.of();
    }
}
