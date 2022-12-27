package com.voidhub.api.event;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.*;
import com.voidhub.api.entity.Event;
import com.voidhub.api.repository.EventRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public class UpdateEventTest extends BaseTest {

    private @Autowired EventRepository eventRepository;
    private @Autowired EventUtil eventUtil;
    private @Autowired UserUtil userUtil;

    private final String eventWriteAuth = "event:write";

    @AfterEach
    public void afterEach() {
        eventUtil.clearEvents();
    }

    @Test
    public void rolesWithWriteEventAuthorityExists() {
        Assertions.assertTrue(Util.getRolesWithAuthority(eventWriteAuth).size() > 0);
    }

    @Test
    public void rolesWithoutWriteEventAuthorityExists() {
        Assertions.assertTrue(Util.getRolesWithoutAuthority(eventWriteAuth).size() > 0);
    }

    @Test
    public void userWithEventWriteAuthority_UpdatesEventTitle_ReturnsOk() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuth, port)) {
            Event event = eventUtil.createAndSaveEvent(user.user());

            RestAssured.given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body("{\"title\": \"new title\"}")
                    .when()
                    .put("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(200);

            Event updatedEvent = eventRepository.findById(event.getId()).get();

            Assertions.assertEquals("new title", updatedEvent.getTitle());
            Assertions.assertEquals(event.getShortDescription(), updatedEvent.getShortDescription());

            RestAssured.given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body("{\"title\": \"new new title\", \"shortDescription\": \"new short description\"}")
                    .when()
                    .put("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(200);

            updatedEvent = eventRepository.findById(event.getId()).get();

            Assertions.assertEquals("new new title", updatedEvent.getTitle());
            Assertions.assertEquals("new short description", updatedEvent.getShortDescription());
            Assertions.assertEquals(event.getFullDescription(), updatedEvent.getFullDescription());
            Assertions.assertEquals(event.getStartingDate(), updatedEvent.getStartingDate());
            Assertions.assertEquals(event.getApplicationDeadline(), updatedEvent.getApplicationDeadline());
            Assertions.assertEquals(event.getPublishedBy().getUsername(), updatedEvent.getPublishedBy().getUsername());
            Assertions.assertEquals(event.getCreatedAt(), updatedEvent.getCreatedAt());
            Assertions.assertNotEquals(event.getUpdatedAt(), updatedEvent.getUpdatedAt());

            eventUtil.clearEvents();
        }
    }

    @Test
    public void userWithEventWriteAuthority_UpdatesNonExistingEvent_ReturnsNotFound() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuth, port)) {
            RestAssured.given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body("{\"title\": \"new title\"}")
                    .when()
                    .put("api/v1/events/" + UUID.randomUUID())
                    .then()
                    .statusCode(404);
        }
    }

    @Test
    public void userWithEventWriteAuthority_UpdatesOthersEvent_ReturnsForbidden() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuth, port)) {
            TestUser publisher = userUtil.getUserWithAuthority(eventWriteAuth, port);
            Event event = eventUtil.createAndSaveEvent(publisher.user());

            RestAssured.given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body("{\"title\": \"new title\"}")
                    .when()
                    .put("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("You did not publish this event"));

            eventUtil.clearEvents();
        }
    }

    @Test
    public void userWithoutWriteEventAuthority_UpdatesEventTitle_ReturnsUnauthorized() {
        for (TestUser user : userUtil.getUsersWithoutAuthority(eventWriteAuth, port)) {
            Event event = eventUtil.createAndSaveEvent(user.user());

            RestAssured.given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body("{\"title\": \"new title\"}")
                    .when()
                    .put("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(401);

            Assertions.assertEquals("title", eventRepository.findById(event.getId()).get().getTitle());
            eventUtil.clearEvents();
        }
    }

    @Test
    public void unauthorizedUser_UpdatesEventTitle_ReturnsUnauthorized() {
        TestUser user = userUtil.getUserWithoutAuthority(eventWriteAuth, port);
        Event event = eventUtil.createAndSaveEvent(user.user());

        RestAssured.given()
                .contentType("application/json")
                .body("{\"title\": \"new title\"}")
                .when()
                .put("api/v1/events/" + event.getId())
                .then()
                .statusCode(401);

        Assertions.assertEquals("title", eventRepository.findById(event.getId()).get().getTitle());
    }

}
