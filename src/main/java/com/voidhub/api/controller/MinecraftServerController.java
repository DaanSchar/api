package com.voidhub.api.controller;

import com.voidhub.api.entity.MinecraftUserInfo;
import com.voidhub.api.service.MinecraftServerService;
import com.voidhub.api.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/server")
public class MinecraftServerController {

    private @Autowired MinecraftServerService minecraftServerService;

    @PostMapping("/join")
    @PreAuthorize("hasAuthority('file:write')")
    public ResponseEntity<Message> joinServer(@RequestBody MinecraftUserInfo body) {
        return minecraftServerService.joinServer(body.getId());
    }

    @PostMapping("/leave")
    @PreAuthorize("hasAuthority('file:write')")
    public ResponseEntity<Message> leaveServer(@RequestBody MinecraftUserInfo body) {
        return minecraftServerService.leaveServer(body.getId());
    }

}
