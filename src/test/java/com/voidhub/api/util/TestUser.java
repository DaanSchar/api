package com.voidhub.api.util;

import com.voidhub.api.entity.User;
import lombok.Getter;

public class TestUser {

    private User user;
    private String unEncodedPassword;
    private String token;

    public TestUser(User user, String unEncodedPassword, String token) {
        this.user = user;
        this.unEncodedPassword = unEncodedPassword;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public String getUnEncodedPassword() {
        return unEncodedPassword;
    }
}
