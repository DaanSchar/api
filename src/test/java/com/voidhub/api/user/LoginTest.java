package com.voidhub.api.user;

import com.voidhub.api.BaseTest;
import com.voidhub.api.entity.Role;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

public class LoginTest extends BaseTest {

    private @Autowired UserUtil userUtil;

    @Test
    public void successfulLogin() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"" + member.getUsername() + "\", \"password\": \"" + member.unEncodedPassword() + "\"}")
                .when()
                .post("/login")
                .then()
                .header("Authorization", notNullValue())
                .header("Authorization", startsWith("Bearer "))
                .statusCode(200);
    }

    @Test
    public void unsuccessfulLoginUsingWrongPassword() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"" + member.getUsername() + "\", \"password\": \"wrongpassword\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingWrongUsername() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"usernameee\", \"password\": \"" + member.unEncodedPassword() + "\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingWrongUsernameAndPassword() {
        userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"usernameee\", \"password\": \"wrongpassword\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingEmptyUsernameAndPassword() {
        userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"\", \"password\": \"\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingNonExistingCredentials() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"username\", \"password\": \"password\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

}
