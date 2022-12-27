package com.voidhub.api.event;


import com.voidhub.api.entity.Role;
import com.voidhub.api.entity.User;
import com.voidhub.api.form.create.CreateEventForm;
import com.voidhub.api.repository.EventRepository;
import com.voidhub.api.repository.UserRepository;
import com.voidhub.api.util.Util;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

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
    public void roleWithWriteEventAuthorityExists() {
        Assertions.assertTrue(Util.getRolesWithAuthority(eventWriteAuth).size() > 0);
    }

    @Test
    public void rolesWithoutWriteEventAuthorityExist() {
        Assertions.assertTrue(Util.getRolesWithoutAuthority(eventWriteAuth).size() > 0);
    }

    @Test
    public void userWithEventWriteAuthority_CreatesNewEvent_ReturnsOkWithId() {
        for (Role role : Util.getRolesWithAuthority(eventWriteAuth)) {
            eventRepository.deleteAll();

            buildAndWriteUser(role);
            var eventForm = buildEventForm();

            var result = RestAssured.given()
                    .given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .contentType("application/json")
                    .body(Util.toBody(eventForm))
                    .when()
                    .post("/api/v1/events")
                    .then();
            result
                    .statusCode(201)
                    .body("message", equalTo("Successfully created event"));

            Assertions.assertEquals(1, eventRepository.findAll().size());

            UUID id = eventRepository.findAll().get(0).getId();

            result.header("Location", equalTo("/api/v1/events/" + id));
        }
    }

    @Test
    public void userWithEventWriteAuthority_CreatesNewEventWithInvalidDate_Returns422() {
        Assertions.assertTrue(Util.roleWithAuthorityExists(eventWriteAuth));

        for (Role role : Util.getRolesWithAuthority(eventWriteAuth)) {
            buildAndWriteUser(role);
            RestAssured.given()
                    .given()
                    .header("Authorization", Util.getToken(username, password, port))
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
        for (Role role : Util.getRolesWithoutAuthority(eventWriteAuth)) {
            buildAndWriteUser(role);
            var eventForm = buildEventForm();

            RestAssured.given()
                    .given()
                    .header("Authorization", Util.getToken(username, password, port))
                    .contentType("application/json")
                    .body(Util.toBody(eventForm))
                    .when()
                    .post("/api/v1/events")
                    .then()
                    .statusCode(401);
        }
    }

    private User buildAndWriteUser(Role role) {
        return userRepository.save(User.builder()
                .role(role)
                .password(passwordEncoder.encode(password))
                .username(username)
                .build()
        );
    }

    private CreateEventForm buildEventForm() {
        return CreateEventForm.builder()
                .title("title")
                .shortDescription("short")
                .fullDescription("full")
                .applicationDeadline(Util.getRandomFutureDate())
                .startingDate(Util.getRandomFutureDate())
                .build();
    }

}
