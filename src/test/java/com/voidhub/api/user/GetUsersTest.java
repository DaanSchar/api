package com.voidhub.api.user;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.TestUser;
import com.voidhub.api.util.UserUtil;
import com.voidhub.api.entity.Role;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.*;

import static org.hamcrest.Matchers.equalTo;

public class GetUsersTest extends BaseTest {

    private @Autowired UserUtil userUtil;

    @Test
    public void adminUserRequestsAllUsersReturnsOk() {
        userUtil.clearUsers();
        TestUser admin = userUtil.getUserWithRole(Role.ADMIN, port);
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .header(admin.getAuthHeader())
                .get("api/v1/users")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].username", equalTo(admin.getUsername()))
                .body("[1].username", equalTo(member.getUsername()))
                .body("[0].password", equalTo(null))
                .body("[0].password", equalTo(null));
    }

    @Test
    public void memberRequestsAllUsersReturnsForbidden() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .header(member.getAuthHeader())
                .get("api/v1/users")
                .then()
                .body("message", equalTo("Access Denied"))
                .statusCode(401);
    }

    @Test
    public void unauthenticatedUserRequestsAllUsersReturnsForbidden() {
        RestAssured.get("/api/v1/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void adminUserGetsExistingUserReturnsOk() {
        TestUser admin = userUtil.getUserWithRole(Role.ADMIN, port);

        RestAssured.given()
                .header(admin.getAuthHeader())
                .get("api/v1/users/" + admin.getUsername())
                .then()
                .statusCode(200)
                .body("username", equalTo(admin.getUsername()))
                .body("password", equalTo(null))
                .body("role", equalTo(null));
    }

    @Test
    public void memberUserGetsExistingUserReturnsOk() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .header(member.getAuthHeader())
                .get("/api/v1/users/" + member.getUsername())
                .then()
                .body("username", equalTo(member.getUsername()))
                .body("password", equalTo(null))
                .body("role", equalTo(null))
                .statusCode(200);
    }

    @Test
    public void adminUserGetsNonExistingUserReturnsNotFound() {
        TestUser admin = userUtil.getUserWithRole(Role.ADMIN, port);

        RestAssured.given()
                .header(admin.getAuthHeader())
                .get("/api/v1/users/nonexisting")
                .then()
                .statusCode(404);
    }

    @Test
    public void memberUserGetsNonExistingUserReturnsNotFound() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured.given()
                .header(member.getAuthHeader())
                .get("/api/v1/users/nonexisting")
                .then()
                .statusCode(404);
    }

    @Test
    public void unauthenticatedUserGetsExistingUserReturnsForbidden() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        RestAssured
                .get("/api/v1/users/" + member.getUsername())
                .then()
                .statusCode(401);
    }

    @Test
    public void unauthenticatedUserGetsNonExistingUserReturnsForbidden() {
        RestAssured
                .get("/api/v1/users/username")
                .then()
                .statusCode(401);
    }

}
