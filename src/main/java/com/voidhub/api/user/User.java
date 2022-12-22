package com.voidhub.api.user;


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

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Id
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(columnDefinition = "varchar(255) default 'MEMBER'")
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.MEMBER;

}
