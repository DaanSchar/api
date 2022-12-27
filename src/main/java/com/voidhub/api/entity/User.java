package com.voidhub.api.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(columnDefinition = "varchar(255) default 'MEMBER'")
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.MEMBER;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private UserInfo userInfo;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
