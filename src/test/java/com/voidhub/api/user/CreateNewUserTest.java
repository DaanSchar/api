package com.voidhub.api.user;


import com.voidhub.api.BaseTest;
import com.voidhub.api.entity.*;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.Matchers.*;

public class CreateNewUserTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {
        super.beforeEach();
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
        return "{" +
                "\"username\": \"" + username + "\"," +
                " \"password\": \"" + password + "\"," +
                "\"discordName\": \"John#1111\"," +
                "\"email\": \"john.man@gmail.com\"," +
                "\"minecraftName\": \"I_Always_Pvp\"" +
                "}";
    }

}
