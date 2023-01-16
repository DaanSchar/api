package com.voidhub.api.repository;

import com.voidhub.api.entity.MinecraftUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MinecraftUserInfoRepository extends JpaRepository<MinecraftUserInfo, UUID> {
}
