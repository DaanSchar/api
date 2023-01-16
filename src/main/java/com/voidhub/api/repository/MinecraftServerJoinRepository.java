package com.voidhub.api.repository;

import com.voidhub.api.entity.MinecraftServerJoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MinecraftServerJoinRepository extends JpaRepository<MinecraftServerJoin, BigInteger> {
}
