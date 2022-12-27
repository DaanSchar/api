package com.voidhub.api.user;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.TestUser;
import com.voidhub.api.util.UserUtil;
import com.voidhub.api.entity.*;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;

public class DeleteUserTest extends BaseTest {

    private @Autowired UserRepository userRepository;
    private @Autowired UserUtil userUtil;

    @Test
    public void adminDeletesUserReturnsOk() {
        TestUser admin = userUtil.getUserWithRole(Role.ADMIN, port);
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .header("Authorization", admin.token())
                .delete("api/v1/users/" + member.getUsername())
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted user"));

        Assertions.assertTrue(userRepository.findById(member.getUsername()).isEmpty());
    }

    @Test
    public void adminDeletesNonExistingUserReturnsNotFound() {
        TestUser admin = userUtil.getUserWithRole(Role.ADMIN, port);

        RestAssured.given()
                .header("Authorization", admin.token())
                .delete("/api/v1/users/member")
                .then()
                .statusCode(404)
                .body("message", equalTo("User does not exist"));
    }

    @Test
    public void memberDeletesUserReturnsUnauthorized() {
        TestUser admin = userUtil.getUserWithRole(Role.ADMIN, port);
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .header(member.getAuthHeader())
                .delete("/api/v1/users/" + admin.getUsername())
                .then()
                .statusCode(401);
    }

    @Test
    public void unauthorizedUserDeletesUserReturnsUnauthorized() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .delete("/api/v1/users/" + member.getUsername())
                .then()
                .statusCode(401);
    }

    @Test
    public void unauthorizedUserDeletesItselfReturnsUnauthorized() {
        userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .delete("api/v1/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void memberDeletesItselfReturnsOk() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .header(member.getAuthHeader())
                .delete("api/v1/users")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted user"));

        Assertions.assertTrue(userRepository.findById(member.getUsername()).isEmpty());
    }

    @Test
    public void adminDeletesItselfReturnsOk() {
        TestUser admin = userUtil.getUserWithRole(Role.ADMIN, port);

        RestAssured.given()
                .header(admin.getAuthHeader())
                .delete("/api/v1/users")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted user"));

        Assertions.assertTrue(userRepository.findById(admin.getUsername()).isEmpty());
    }

}
