package com.voidhub.api.event;

import com.voidhub.api.Util;
import com.voidhub.api.user.Role;
import com.voidhub.api.user.User;
import com.voidhub.api.user.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UpdateEventTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String eventWriteAuth = "event:write";
    private static String username;
    private static String password;

    @BeforeAll
    public static void beforeAll() {
        username = UUID.randomUUID().toString();
        password = UUID.randomUUID().toString();
        RestAssured.baseURI = "http://localhost";
    }

    @BeforeEach
    public void beforeEach() {
        RestAssured.port = port;

        // order matters here. cannot delete users if events still exist,
        // as there is a foreign key constraint
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
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
        for (Role role : Util.getRolesWithAuthority(eventWriteAuth)) {
            eventRepository.deleteAll();
            userRepository.deleteAll();

            User user = userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());

            Event event = saveEvent(user);

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
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
                    .header("Authorization", Util.getToken(username, password, port))
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
        }
    }

    @Test
    public void userWithEventWriteAuthority_UpdatesNonExistingEvent_ReturnsNotFound() {
        for (Role role : Util.getRolesWithAuthority(eventWriteAuth)) {
            userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
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
        for (Role role : Util.getRolesWithAuthority(eventWriteAuth)) {
            eventRepository.deleteAll();
            userRepository.deleteAll();

            User publisher = userRepository.save(User.builder()
                    .username("username")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .build());

            Event event = saveEvent(publisher);

            userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .contentType("application/json")
                    .body("{\"title\": \"new title\"}")
                    .when()
                    .put("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(403)
                    .body("message", equalTo("You did not publish this event"));
        }
    }

    @Test
    public void userWithoutWriteEventAuthority_UpdatesEventTitle_ReturnsUnauthorized() {
        for (Role role : Util.getRolesWithoutAuthority(eventWriteAuth)) {
            eventRepository.deleteAll();
            userRepository.deleteAll();

            User user = userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());

            Event event = saveEvent(user);

            RestAssured.given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .contentType("application/json")
                    .body("{\"title\": \"new title\"}")
                    .when()
                    .put("api/v1/events/" + event.getId())
                    .then()
                    .statusCode(401);

            Assertions.assertEquals("title", eventRepository.findById(event.getId()).get().getTitle());
        }
    }

    @Test
    public void unauthorizedUser_UpdatesEventTitle_ReturnsUnauthorized() {
        User user = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.MEMBER)
                .build());

        Event event = saveEvent(user);

        RestAssured.given()
                .contentType("application/json")
                .body("{\"title\": \"new title\"}")
                .when()
                .put("api/v1/events/" + event.getId())
                .then()
                .statusCode(401);

        Assertions.assertEquals("title", eventRepository.findById(event.getId()).get().getTitle());
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
