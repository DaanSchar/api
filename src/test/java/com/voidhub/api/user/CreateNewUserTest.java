package com.voidhub.api.user;


import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CreateNewUserTest {

    @Autowired
    private UserRepository userRepository;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        userRepository.deleteAll();
    }

    @Test
    public void CreateNewUserReturnsOkAndSavesUser() {
        RestAssured.given()
                .contentType("application/json")
                .body(toBody("username", "123validPassword!"))
                .when()
                .post("api/v1/users")
                .then()
                .body("message", equalTo("Successfully created user"))
                .statusCode(200);

        User user = userRepository.findById("username").get();

        Assertions.assertEquals("username", user.getUsername());
        Assertions.assertTrue(passwordEncoder.matches("123validPassword!", user.getPassword()));
        Assertions.assertEquals(user.getRole(), Role.MEMBER);
    }

    @Test
    public void CreateNewUserUsingEmptyBodyReturnsBadRequest() {
        RestAssured.given()
                .contentType("application/json")
                .body("{}")
                .when()
                .post("api/v1/users")
                .then()
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("username"))
                .body("errors", hasKey("password"))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    public void createNewUserUsingEmptyUsernameReturnsBadRequest() {
        RestAssured.given()
                .contentType("application/json")
                .body(toBody("", ""))
                .when()
                .post("/api/v1/users")
                .then()
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("username"))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    public void createNewUserWithAlreadyExistingUsernameReturnsConflict() {
        String requestBody = toBody("username", "valid4Passwords!");

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:" + port + "/api/v1/users")
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo("Username already exists"));
    }

    @Test
    public void createNewUserUsingEmptyPasswordReturnsBadRequest() {
        RestAssured.given()
                .contentType("application/json")
                .body(toBody("username", ""))
                .when()
                .post("/api/v1/users")
                .then()
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("password"))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }

    private String toBody(String username, String password) {
        return "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
    }

}
