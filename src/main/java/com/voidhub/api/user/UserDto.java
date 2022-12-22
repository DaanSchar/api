package com.voidhub.api.user;

import lombok.Data;

@Data
public class UserDto {

    public UserDto(User user) {
        this.username = user.getUsername();
    }

    private String username;

}
