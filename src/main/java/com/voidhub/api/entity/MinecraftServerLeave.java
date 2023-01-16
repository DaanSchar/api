package com.voidhub.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "minecraft_serverleaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinecraftServerLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private UUID minecraftUserId;

    @Column(nullable = false)
    private Date date;

    public MinecraftServerLeave(UUID minecraftUserId, Date date) {
        this.minecraftUserId = minecraftUserId;
        this.date = date;
    }

}
