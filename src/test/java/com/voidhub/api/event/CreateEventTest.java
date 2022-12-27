package com.voidhub.api.event;


import com.voidhub.api.BaseTest;
import com.voidhub.api.repository.EventRepository;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class CreateEventTest extends BaseTest {

    private @Autowired EventRepository eventRepository;
    private @Autowired EventUtil eventUtil;
    private @Autowired UserUtil userUtil;

    private final String eventWriteAuth = "event:write";

    @AfterEach
    public void afterEach() {
        eventUtil.clearEvents();
    }

    @Test
    public void roleWithWriteEventAuthorityExists() {
        Assertions.assertTrue(Util.getRolesWithAuthority(eventWriteAuth).size() > 0);
    }

    @Test
    public void rolesWithoutWriteEventAuthorityExist() {
        Assertions.assertTrue(Util.getRolesWithoutAuthority(eventWriteAuth).size() > 0);
    }

    @Test
    public void userWithEventWriteAuthority_CreatesNewEvent_ReturnsOkWithId() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuth, port)) {
            var result = RestAssured.given()
                    .given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body(Util.toBody(eventUtil.getRandomValidEventForm()))
                    .when()
                    .post("/api/v1/events")
                    .then()
                    .statusCode(201)
                    .body("message", equalTo("Successfully created event"));

            Assertions.assertEquals(1, eventRepository.findAll().size());

            UUID id = eventRepository.findAll().get(0).getId();

            result.header("Location", equalTo("/api/v1/events/" + id));

            eventUtil.clearEvents();
        }
    }

    @Test
    public void userWithEventWriteAuthority_CreatesNewEventWithInvalidDate_Returns422() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuth, port)) {
            RestAssured.given()
                    .given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body("{}")
                    .when()
                    .post("/api/v1/events")
                    .then()
                    .statusCode(422)
                    .body("message", equalTo("Invalid request"));

            Assertions.assertEquals(0, eventRepository.findAll().size());
        }
    }

    @Test
    public void UserWithoutWriteEventAuthority_CreatesNewEvent_ReturnsNotAuthorized() {
        for (TestUser user : userUtil.getUsersWithoutAuthority(eventWriteAuth, port)) {
            RestAssured.given()
                    .given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body(Util.toBody(eventUtil.getRandomValidEventForm()))
                    .when()
                    .post("/api/v1/events")
                    .then()
                    .statusCode(401);
        }
    }

    @Test
    public void userWithWriteEventAuthority_CreatesNewEventWithMissingImage_ReturnsNotFound() {
        for (TestUser user : userUtil.getUsersWithAuthority(eventWriteAuth, port)) {
            var form = eventUtil.getRandomValidEventForm();
            form.setImageId(UUID.randomUUID());

            RestAssured.given()
                    .given()
                    .header("Authorization", user.token())
                    .contentType("application/json")
                    .body(Util.toBody(form))
                    .when()
                    .post("/api/v1/events")
                    .then()
                    .statusCode(404)
                    .body("message", equalTo("Image does not exist"));
        }
    }

}
