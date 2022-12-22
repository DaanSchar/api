package com.voidhub.api.user.endpoints;


import com.voidhub.api.user.*;
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
        userRepository.deleteAll();
    }

    @Test
    public void CreateNewUserReturnsOkAndSavesUser() {
        String requestBody = "{\"username\": \"username\", \"password\": \"123validPassword!\"}";

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:" + port + "/api/v1/users")
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
        String requestBody = "{\"username\": \"\", \"password\": \"\"}";

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:" + port + "/api/v1/users")
                .then()
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("password"))
                .body("errors", hasKey("username"))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        requestBody = "{}";

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:" + port + "/api/v1/users")
                .then()
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("username"))
                .body("errors", hasKey("password"))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    public void createNewUserUsingEmptyUsernameReturnsBadRequest() {
        String requestBody = "{\"username\": \"\", \"password\": \"password\"}";

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:" + port + "/api/v1/users")
                .then()
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("username"))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    public void createNewUserUsingEmptyPasswordReturnsBadRequest() {
        String requestBody = "{\"username\": \"username\", \"password\": \"\"}";

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("http://localhost:" + port + "/api/v1/users")
                .then()
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("password"))
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }

}
