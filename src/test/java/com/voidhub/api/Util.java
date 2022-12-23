package com.voidhub.api;

import io.restassured.RestAssured;

public class Util {

    public static String getToken(String username, String password, int port) {
        String body = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        return RestAssured
                .given()
                .contentType("application/json")
                .body(body)
                .post("http://localhost:" + port + "/login")
                .getHeader("Authorization");
    }

}
