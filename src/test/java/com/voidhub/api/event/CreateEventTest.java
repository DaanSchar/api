package com.voidhub.api.event;


import com.voidhub.api.Util;
import com.voidhub.api.user.Role;
import com.voidhub.api.user.User;
import com.voidhub.api.user.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CreateEventTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void adminCreatesNewEventReturnsOkWithId() {
        userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        var eventForm = NewEventForm.builder()
                .title("title")
                .shortDescription("short")
                .fullDescription("full")
                .applicationDeadline(new Date())
                .startingDate(new Date())
                .build();

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .contentType("application/json")
                .body(toBody(eventForm))
                .when()
                .post("/api/v1/events")
                .then()
                .body("id", notNullValue())
                .header("Location", contains("/api/v1/events/"))
                .body("message", equalTo("Successfully created event"))
                .statusCode(201);

        var result = RestAssured.given()
                .given()
                .header("Authorization", Util.getToken("username", "password", port))
                .contentType("application/json")
                .body(toBody(eventForm))
                .when()
                .post("/api/v1/events")
                .then();

        result
                .statusCode(201)
                .body("message", equalTo("Successfully created event"));

        Assertions.assertEquals(1, eventRepository.findAll().size());

        UUID id = eventRepository.findAll().get(0).getId();

        result.header("Location", contains("/api/v1/events/" + id));
    }

    private String toBody(NewEventForm newEvent) {
        return "{" +
                "\"title\": \"" + newEvent.getTitle() + "\", " +
                "\"shortDescription\": \"" + newEvent.getShortDescription() + "\", " +
                "\"fullDescription\": \"" + newEvent.getFullDescription() + "\", " +
                "\"applicationDeadline\": \"" + newEvent.getApplicationDeadline() + "\", " +
                "\"startingDate\": \"" + newEvent.getStartingDate() + "\"" +
                "}";
    }

}
