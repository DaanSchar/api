package com.voidhub.api.user;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.*;
import com.voidhub.api.entity.*;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;

import static org.hamcrest.Matchers.*;

public class UpdateUserRoleTest extends BaseTest {

    private final String USER_WRITE_AUTH = "user:write";

    private @Autowired UserRepository userRepository;
    private @Autowired UserUtil userUtil;

    @Test
    public void userWithWriteUserAuthority_UpdatesUserRole_ReturnsOk() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        for (TestUser user : userUtil.getUsersWithAuthority(USER_WRITE_AUTH, port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .body("{\"username\": \"" + member.getUsername() + "\", \"role\": \"ADMIN\"}")
                    .contentType("application/json")
                    .put("/api/v1/users/role")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("Successfully updated user role"));

            Assertions.assertEquals(Role.ADMIN, userRepository.findById(member.getUsername()).get().getRole());
        }
    }

    @Test
    public void userWithWriteUserAuthority_UpdatesRoleWithDuplicate_ReturnsBadRequest() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        for (TestUser user : userUtil.getUsersWithAuthority(USER_WRITE_AUTH, port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .body(
                            "{\"username\": \"" + member.getUsername() + "\", " +
                            "\"role\": \"" + member.user().getRole().name() + "\"}"
                    )
                    .contentType("application/json")
                    .put("/api/v1/users/role")
                    .then()
                    .statusCode(409)
                    .body("message", equalTo("User already has assigned role"));

            Assertions.assertEquals(Role.MEMBER, userRepository.findById(member.getUsername()).get().getRole());
        }
    }

    @Test
    public void userWithWriteUserAuthority_UpdatesRoleWithInvalidRoleName_ReturnsBadRequest() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        for (TestUser user : userUtil.getUsersWithAuthority(USER_WRITE_AUTH, port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .body("{\"username\": \"" + member.getUsername() +"\", \"role\": \"INVALID\"}")
                    .contentType("application/json")
                    .put("/api/v1/users/role")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("Invalid role"));
        }
    }

    @Test
    public void userWithWriteUserAuthority_UpdatesRoleOfNonExistingUser_ReturnsBadRequest() {
        for (TestUser user : userUtil.getUsersWithAuthority(USER_WRITE_AUTH, port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .body("{\"username\": \"username3\", \"role\": \"ADMIN\"}")
                    .contentType("application/json")
                    .put("/api/v1/users/role")
                    .then()
                    .statusCode(404)
                    .body("message", equalTo("User does not exist"));
        }
    }

    @Test
    public void userWithWriteUserAuthority_UpdatesRoleWithInvalidBody_Returns422() {
        for (TestUser user : userUtil.getUsersWithAuthority(USER_WRITE_AUTH, port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .body("{\"username\": \"username3\"}")
                    .contentType("application/json")
                    .put("/api/v1/users/role")
                    .then()
                    .statusCode(422)
                    .body("message", equalTo("Invalid request"))
                    .body("errors", hasKey("role"));

            RestAssured.given()
                    .header(user.getAuthHeader())
                    .body("{}")
                    .contentType("application/json")
                    .put("/api/v1/users/role")
                    .then()
                    .statusCode(422)
                    .body("message", equalTo("Invalid request"))
                    .body("errors", hasKey("role"));
        }
    }


    @Test
    public void userWithoutWriteUserAuthority_UpdatesRole_ReturnsUnauthorized() {
        TestUser member = userUtil.getUserWithRole(Role.MEMBER, port);

        for (TestUser user : userUtil.getUsersWithoutAuthority(USER_WRITE_AUTH, port)) {

            RestAssured.given()
                    .header(user.getAuthHeader())
                    .body("{\"username\": \"" + member.getUsername() + "\", \"role\": \"ADMIN\"}")
                    .contentType("application/json")
                    .put("/api/v1/users/role")
                    .then()
                    .statusCode(401);
        }
    }

}
