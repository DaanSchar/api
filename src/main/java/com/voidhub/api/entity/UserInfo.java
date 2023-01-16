package com.voidhub.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "userinfos")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String discordName;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private MinecraftUserInfo minecraftUserInfo;

    @Column(nullable = false)
    private boolean isVerified;

    public UserInfo() {}

    public UserInfo(String email, String discordName, MinecraftUserInfo minecraftUserInfo) {
        this.email = email;
        this.discordName = discordName;
        this.minecraftUserInfo = minecraftUserInfo;
    }

    public UserInfo(String email, String discordName, MinecraftUserInfo minecraftUserInfo, boolean isVerified) {
        this.email = email;
        this.discordName = discordName;
        this.minecraftUserInfo = minecraftUserInfo;
        this.isVerified = isVerified;
    }

}
