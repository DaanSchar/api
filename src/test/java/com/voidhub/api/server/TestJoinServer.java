package com.voidhub.api.server;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class TestJoinServer extends BaseTest {

    private @Autowired UserUtil userUtil;

    @Test
    public void AuthorizedUserPostsJoinServerWithExistingUser() {
        for (TestUser user : userUtil.getUsersWithAuthority("minecraft_server:write",port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .contentType("application/json")
                    .body("{ " +
                            "\"name\": \"" + user.user().getUserInfo().getMinecraftUserInfo().getName() + "\"," +
                            "\"id\": \"" + user.user().getUserInfo().getMinecraftUserInfo().getId() + "\"" +
                            " }"
                    )
                    .post("api/v1/server/join")
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    public void AuthorizedUserPostsLeaveServerWithExistingUser() {
        for (TestUser user : userUtil.getUsersWithAuthority("minecraft_server:write",port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .contentType("application/json")
                    .body("{ " +
                            "\"name\": \"" + user.user().getUserInfo().getMinecraftUserInfo().getName() + "\"," +
                            "\"id\": \"" + user.user().getUserInfo().getMinecraftUserInfo().getId() + "\"" +
                            " }"
                    )
                    .post("api/v1/server/leave")
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    public void AuthorizedUserPostsJoinServerWithNonExistingUser() {
        for (TestUser user : userUtil.getUsersWithAuthority("minecraft_server:write",port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .contentType("application/json")
                    .body("{ " +
                            "\"name\": \"" + UUID.randomUUID() + "\"," +
                            "\"id\": \"" + UUID.randomUUID() + "\"" +
                            " }"
                    )
                    .post("api/v1/server/join")
                    .then()
                    .statusCode(404);
        }
    }

    @Test
    public void AuthorizedUserPostsLeaveServerNonWithNonExistingUser() {
        for (TestUser user : userUtil.getUsersWithAuthority("minecraft_server:write",port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .contentType("application/json")
                    .body("{ " +
                            "\"name\": \"" + UUID.randomUUID() + "\"," +
                            "\"id\": \"" + UUID.randomUUID() + "\"" +
                            " }"
                    )
                    .post("api/v1/server/leave")
                    .then()
                    .statusCode(404);
        }
    }

}
