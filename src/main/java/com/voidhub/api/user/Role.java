package com.voidhub.api.user;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Role {

    MEMBER(
            "ROLE_MEMBER",
            "self:write",
            "user:read"
    ),
    ADMIN(
            "ROLE_ADMIN",
            "self:write",
            "user:read",
            "user:write"
    );

    private final List<SimpleGrantedAuthority> authorities;

    Role(String... authorities) {
        this.authorities = Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String toString() {
        return authorities.get(0).getAuthority();
    }

}
