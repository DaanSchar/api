package com.voidhub.api.user;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Getter
public enum Role {

    MEMBER(
            "ROLE_MEMBER",
            "self:write",
            "user:read"
    ),
    EventHost(
            "ROLE_EVENT_HOST",
            "self:write",
            "user:read",
            "event:write"
    ),
    ADMIN(
            "ROLE_ADMIN",
            "self:write",
            "user:read",
            "user:write",
            "event:write"
    );

    private final List<SimpleGrantedAuthority> authorities;

    Role(String... authorities) {
        this.authorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}
