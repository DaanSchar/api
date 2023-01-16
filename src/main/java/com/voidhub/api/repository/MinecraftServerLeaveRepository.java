package com.voidhub.api.repository;

import com.voidhub.api.entity.MinecraftServerLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MinecraftServerLeaveRepository extends JpaRepository<MinecraftServerLeave, BigInteger> {
}
