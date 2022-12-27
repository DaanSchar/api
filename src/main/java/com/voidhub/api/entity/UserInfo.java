package com.voidhub.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String discordName;

    @Column(nullable = false)
    private String minecraftName;

//    @OneToOne(mappedBy = "userInfo")
//    private User user;

    public UserInfo() {}

    public UserInfo(String email, String discordName, String minecraftName) {
        this.email = email;
        this.discordName = discordName;
        this.minecraftName = minecraftName;
    }

}
