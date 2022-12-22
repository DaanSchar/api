package com.voidhub.api.security;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.voidhub.api.security.ApplicationUserPermission.*;

@Getter
public enum ApplicationUserRole {

    STUDENT(Set.of()),
    ADMIN(Set.of(STUDENT_WRITE, STUDENT_READ, COURSE_READ, COURSE_WRITE)),
    ADMIN_TRAINEE(Set.of(STUDENT_READ));

    private final Set<ApplicationUserPermission> permissions;

    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        var permissions = this.getPermissions().stream().map(
                permission -> new SimpleGrantedAuthority(permission.getPermission())
        ).collect(Collectors.toSet());

        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return permissions;
    }
}
