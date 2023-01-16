package com.voidhub.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "minecraft_serverjoins")
@Getter
@Setter
@NoArgsConstructor
public class MinecraftServerJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private UUID minecraftUserId;

    @Column(nullable = false)
    private Date date;

    public MinecraftServerJoin(UUID minecraftUserId, Date date) {
        this.minecraftUserId = minecraftUserId;
        this.date = date;
    }
}
