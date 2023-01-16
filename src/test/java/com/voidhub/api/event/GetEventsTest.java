package com.voidhub.api.event;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.*;
import com.voidhub.api.entity.Event;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetEventsTest extends BaseTest {

    private @Autowired UserUtil userUtil;
    private @Autowired EventUtil eventUtil;

    @Test
    public void unauthenticatedRequest_GetAllEvents_ReturnsListOfEventsAndOk() {
        TestUser publisher = userUtil.getUserWithAuthority("event:write", port);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        RestAssured.get("/api/v1/events")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(event.getId().toString()));
    }

    @Test
    public void anyAuthenticatedUser_GetAllEvents_ReturnsListOfEventsAndOk() {
        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {
            Event event = eventUtil.createAndSaveEvent(user.user());

            RestAssured.given()
                    .header("Authorization", user.token())
                    .get("/api/v1/events")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].id", equalTo(event.getId().toString()));

            eventUtil.clearEvents();
        }
    }

    @Test
    public void unauthenticatedUser_getsSingleEvent_ReturnsEventDTOAndOk() {
        TestUser publisher = userUtil.getUserWithAuthority("event:write",port);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        RestAssured.get("/api/v1/events/" + event.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(event.getId().toString()))
                .body("title", equalTo(event.getTitle()))
                .body("shortDescription", equalTo(event.getShortDescription()))
                .body("fullDescription", equalTo(event.getFullDescription()))
                .body("createdAt", notNullValue())
                .body("applicationDeadline", notNullValue())
                .body("startingDate", notNullValue())
                .body("publishedBy.username", equalTo(publisher.user().getUsername()))
                .body("image", notNullValue());
    }

    @Test
    public void anyAuthenticatedUser_getsSingleEvent_ReturnsEventDTOAndOk() {
        TestUser publisher = userUtil.getUserWithAuthority("event:write", port);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {
            RestAssured.given()
                    .header("Authorization", user.token())
                    .get("/api/v1/events/" + event.getId())
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(event.getId().toString()))
                    .body("title", equalTo(event.getTitle()))
                    .body("shortDescription", equalTo(event.getShortDescription()))
                    .body("fullDescription", equalTo(event.getFullDescription()))
                    .body("createdAt", notNullValue())
                    .body("applicationDeadline", notNullValue())
                    .body("startingDate", notNullValue())
                    .body("publishedBy.username", equalTo(publisher.user().getUsername()))
                    .body("image", notNullValue());
        }
    }

}
