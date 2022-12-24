package com.voidhub.api.event;

import com.voidhub.api.Util;
import com.voidhub.api.entity.Event;
import com.voidhub.api.entity.Role;
import com.voidhub.api.entity.User;
import com.voidhub.api.repository.EventRepository;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DeleteEventTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String eventWriteAuthority = "event:write";
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

    @AfterEach
    public void afterEach() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

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
        for (Role role : Util.getRolesWithAuthority(eventWriteAuthority)) {
            User user = userRepository.save(User.builder()
                    .role(role)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .build()
            );

            Event event = saveEvent(user);

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .when()
                    .delete("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("Successfully deleted event"));
        }
    }

    @Test
    public void userWithWriteEventAuthority_DeletesNonExistingEvent_ReturnsNotFound() {
        for (Role role : Util.getRolesWithAuthority(eventWriteAuthority)) {
            userRepository.save(User.builder()
                    .role(role)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .build()
            );

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .when()
                    .delete("api/v1/events/" + UUID.randomUUID())
                    .then()
                    .statusCode(404)
                    .body("message", equalTo("Event does not exist"));
        }
    }

    @Test
    public void userWithWriteEventAuthority_DeletesOthersEvent_returnsForbidden() {
        User publisher = userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("publisher")
                .build()
        );
        Event event = saveEvent(publisher);

        for (Role role : Util.getRolesWithAuthority(eventWriteAuthority)) {
            userRepository.save(User.builder()
                    .role(role)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .build()
            );

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .when()
                    .delete("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("You did not publish this event"));
        }
    }

    @Test
    public void userWithoutWriteEventAuthority_DeletesEvent_ReturnsUnauthorized() {
        for (Role role : Util.getRolesWithoutAuthority(eventWriteAuthority)) {
            User eventPoster = userRepository.save(User.builder()
                    .role(Role.ADMIN)
                    .password(passwordEncoder.encode("password"))
                    .username("username")
                    .build()
            );

            Event event = saveEvent(eventPoster);

            userRepository.save(User.builder()
                    .role(role)
                    .password(passwordEncoder.encode(password))
                    .username(username)
                    .build()
            );

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .when()
                    .delete("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(401);
        }
    }

    @Test
    public void unauthenticatedUser_DeletesEvent_ReturnsUnauthorized() {
        User user = userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(password))
                .username(username)
                .build()
        );

        Event event = saveEvent(user);

        RestAssured.given()
                .when()
                .delete("api/v1/events/{id}", event.getId())
                .then()
                .statusCode(401);
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
