package com.voidhub.api.event;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.*;
import com.voidhub.api.entity.Event;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;


public class DeleteEventTest extends BaseTest {

    private @Autowired UserUtil userUtil;
    private @Autowired EventUtil eventUtil;

    private final String eventWriteAuthority = "event:write";

    @Test
    public void eventWriteAuthorityExists() {
        Assertions.assertTrue(Util.getRolesWithAuthority(eventWriteAuthority).size() > 0);
    }

    @Test
    public void usersWithoutEventWriteAuthorityExist() {
        Assertions.assertTrue(Util.getRolesWithoutAuthority(eventWriteAuthority).size() > 0);
    }

    @Test
    public void userWithEventWriteAuthority_DeletesOwnEvent_ReturnsOk() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuthority, port)) {
            Event event = eventUtil.createAndSaveEvent(user.user());

            RestAssured.given()
                    .header("Authorization", user.token())
                    .when()
                    .delete("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("Successfully deleted event"));
        }
    }

    @Test
    public void userWithWriteEventAuthority_DeletesNonExistingEvent_ReturnsNotFound() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuthority, port)) {

            RestAssured.given()
                    .header("Authorization", user.token())
                    .when()
                    .delete("api/v1/events/" + UUID.randomUUID())
                    .then()
                    .statusCode(404)
                    .body("message", equalTo("Event does not exist"));
        }
    }

    @Test
    public void userWithWriteEventAuthority_DeletesOthersEvent_returnsForbidden() {
        TestUser publisher = userUtil.getUserWithAuthority(eventWriteAuthority, port);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuthority, port)) {
            RestAssured.given()
                    .header("Authorization", user.token())
                    .when()
                    .delete("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("You did not publish this event"));
        }
    }

    @Test
    public void userWithoutWriteEventAuthority_DeletesEvent_ReturnsUnauthorized() {
        for (TestUser user : userUtil.getUsersWithoutAuthority(eventWriteAuthority, port)) {
            Event event = eventUtil.createAndSaveEvent(user.user());

            RestAssured.given()
                    .header("Authorization", user.token())
                    .when()
                    .delete("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(401);
        }
    }

    @Test
    public void unauthenticatedUser_DeletesEvent_ReturnsUnauthorized() {
        TestUser publisher = userUtil.getUserWithAuthority(eventWriteAuthority, port);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        RestAssured.given()
                .when()
                .delete("api/v1/events/{id}", event.getId())
                .then()
                .statusCode(401);
    }

}
