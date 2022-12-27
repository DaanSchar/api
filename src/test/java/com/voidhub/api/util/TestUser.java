package com.voidhub.api.util;

import com.voidhub.api.entity.User;
import io.restassured.http.Header;

public record TestUser(User user, String unEncodedPassword, String token) {

    public String getUsername() {
        return user.getUsername();
    }

    public Header getAuthHeader() {
        return new Header("Authorization", token);
    }
}
