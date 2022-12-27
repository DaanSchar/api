package com.voidhub.api.dto;

import com.voidhub.api.entity.User;
import lombok.Getter;

public class UserDto {

    public UserDto(User user) {
        this.username = user.getUsername();
    }

    private @Getter String username;

}
