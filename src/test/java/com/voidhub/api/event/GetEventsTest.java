package com.voidhub.api.event;

import com.voidhub.api.Util;
import com.voidhub.api.user.Role;
import com.voidhub.api.user.User;
import com.voidhub.api.user.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GetEventsTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String username;
    private String password;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setUp() {
        username = UUID.randomUUID().toString();
        password = UUID.randomUUID().toString();

        eventRepository.deleteAll();
        userRepository.deleteAll();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void unauthenticatedRequest_GetAllEvents_ReturnsListOfEventsAndOk() {
        User user = userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(password))
                .username(username)
                .build()
        );

        Event event = saveEvent(user);

        RestAssured.get("/api/v1/events")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(event.getId().toString()));
    }

    @Test
    public void anyAuthenticatedUser_GetAllEvents_ReturnsListOfEventsAndOk() {
        for (Role role : Role.values()) {
            eventRepository.deleteAll();
            User user = userRepository.save(User.builder()
                    .role(role)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .build()
            );

            Event event = saveEvent(user);

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .get("/api/v1/events")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].id", equalTo(event.getId().toString()));
        }
    }

    @Test
    public void unauthenticatedUser_getsSingleEvent_ReturnsEventDTOAndOk() {
        User user = userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode(password))
                .username(username)
                .build()
        );

        Event event = saveEvent(user);

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
                .body("publishedBy.username", equalTo(user.getUsername()));
    }

    @Test
    public void anyAuthenticatedUser_getsSingleEvent_ReturnsEventDTOAndOk() {
        for (Role role : Role.values()) {
            eventRepository.deleteAll();
            User user = userRepository.save(User.builder()
                    .role(role)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .build()
            );

            Event event = saveEvent(user);

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
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
                    .body("publishedBy.username", equalTo(user.getUsername()));
        }
    }

    private Event saveEvent(User user) {
        return eventRepository.save(new Event(
                UUID.randomUUID(),
                "title",
                "shortDescription",
                "fullDescription",
                Util.getRandomFutureDate(),
                Util.getRandomFutureDate(),
                Util.getRandomFutureDate(),
                Util.getRandomFutureDate(),
                user
        ));
    }

}
