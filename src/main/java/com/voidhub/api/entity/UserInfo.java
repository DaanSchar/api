package com.voidhub.api.entity;

import com.voidhub.api.form.EventApplicationForm;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
//@Builder
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

    @Column(nullable = false)
    private boolean isVerified;

    @ManyToMany(mappedBy = "applications")
    private Set<Event> events;

    public UserInfo() {}

    public UserInfo(String email, String discordName, String minecraftName) {
        this.email = email;
        this.discordName = discordName;
        this.minecraftName = minecraftName;
    }

    public UserInfo(String email, String discordName, String minecraftName, boolean isVerified) {
        this.email = email;
        this.discordName = discordName;
        this.minecraftName = minecraftName;
        this.isVerified = isVerified;
    }

    public UserInfo(EventApplicationForm form) {
        this.email = form.getEmail();
        this.discordName = form.getDiscordName();
        this.minecraftName = form.getMinecraftName();
    }

}
