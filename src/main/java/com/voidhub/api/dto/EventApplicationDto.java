package com.voidhub.api.dto;

import com.voidhub.api.entity.UserInfo;
import lombok.Data;

import java.util.UUID;

@Data
public class EventApplicationDto {
    private UUID eventId;
    private UserInfo userInfo;
    private boolean accepted;
}
