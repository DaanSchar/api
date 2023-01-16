package com.voidhub.api.service;

import com.voidhub.api.entity.MinecraftServerJoin;
import com.voidhub.api.entity.MinecraftServerLeave;
import com.voidhub.api.repository.MinecraftServerJoinRepository;
import com.voidhub.api.repository.MinecraftServerLeaveRepository;
import com.voidhub.api.repository.MinecraftUserInfoRepository;
import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class MinecraftServerService {

    private @Autowired MinecraftUserInfoRepository minecraftUserInfoRepository;
    private @Autowired MinecraftServerJoinRepository minecraftServerJoinRepository;
    private @Autowired MinecraftServerLeaveRepository minecraftServerLeaveRepository;

    public ResponseEntity<Message> joinServer(UUID minecraftUserId) {
        if (!minecraftUserInfoRepository.existsById(minecraftUserId)) {
            throw new EntityNotFoundException("Minecraft user does not exist");
        }

        minecraftServerJoinRepository.save(new MinecraftServerJoin(minecraftUserId, new Date()));

        return ResponseEntity.ok(new Message("Successfully joined server"));
    }

    public ResponseEntity<Message> leaveServer(UUID minecraftUserId) {
        if (!minecraftUserInfoRepository.existsById(minecraftUserId)) {
            throw new EntityNotFoundException("Minecraft user does not exist");
        }

        minecraftServerLeaveRepository.save(new MinecraftServerLeave(minecraftUserId, new Date()));

        return ResponseEntity.ok(new Message("Successfully left server"));
    }
}
