package com.voidhub.api.user;

import com.voidhub.api.entity.Role;
import com.voidhub.api.entity.User;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    private int port;

    @Test
    public void successfulLogin() {
        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"username\", \"password\": \"password\"}")
                .when()
                .post("/login")
                .then()
                .header("Authorization", notNullValue())
                .header("Authorization", startsWith("Bearer "))
                .statusCode(200);
    }

    @Test
    public void unsuccessfulLoginUsingWrongPassword() {
        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"username\", \"password\": \"wrongpassword\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingWrongUsername() {
        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"usernameee\", \"password\": \"password\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingWrongUsernameAndPassword() {
        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"usernameee\", \"password\": \"wrongpassword\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingEmptyUsernameAndPassword() {
        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"\", \"password\": \"\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void unsuccessfulLoginUsingNonExistingCredentials() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"username\", \"password\": \"password\"}")
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        userRepository.deleteAll();
    }

}
