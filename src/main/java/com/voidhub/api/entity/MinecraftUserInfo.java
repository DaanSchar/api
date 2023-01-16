package com.voidhub.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "minecraft_userinfos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinecraftUserInfo {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;
}
