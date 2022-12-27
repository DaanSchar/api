package com.voidhub.api.entity;

import jakarta.persistence.Column;

public class UserInfo {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String discordName;

    @Column(nullable = false)
    private String minecraftName;

}
