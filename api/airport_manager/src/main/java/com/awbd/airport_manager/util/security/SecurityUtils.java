package com.awbd.airport_manager.util.security;

import com.awbd.airport_manager.config.security.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserInfo getCurrentUserInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = Objects.requireNonNull(context.getAuthentication());
        return (UserInfo) auth.getPrincipal();
    }

    public static UserInfo extractUserInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        return principal instanceof UserInfo userInfo ? userInfo : null;
    }

    public static boolean hasRole(String role) {
        UserInfo userInfo = extractUserInfo();
        return userInfo != null && userInfo.getRoles().contains(role);
    }

    public static boolean hasAnyRole(String... roles) {
        UserInfo userInfo = extractUserInfo();
        if (userInfo == null) return false;
        for (String role : roles) {
            if (userInfo.getRoles().contains(role)) return true;
        }
        return false;
    }

    public static boolean hasAuthority(String authority) {
        UserInfo userInfo = extractUserInfo();
        return userInfo != null && userInfo.getAuthorities().contains(new SimpleGrantedAuthority(authority));
    }
}
