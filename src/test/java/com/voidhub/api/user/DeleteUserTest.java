package com.voidhub.api.user;

import com.voidhub.api.Util;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DeleteUserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void adminDeletesUserReturnsOk() {
        userRepository.save(new User("admin", passwordEncoder.encode("password"), Role.ADMIN));
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .header("Authorization", Util.getToken("admin", "password", port))
                .delete("http://localhost:" + port + "/api/v1/users/member")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted user"));

        Assertions.assertTrue(userRepository.findById("member").isEmpty());
    }

    @Test
    public void adminDeletesNonExistingUserReturnsNotFound() {
        userRepository.save(new User("admin", passwordEncoder.encode("password"), Role.ADMIN));

        RestAssured.given()
                .header("Authorization", Util.getToken("admin", "password", port))
                .delete("http://localhost:" + port + "/api/v1/users/member")
                .then()
                .statusCode(404)
                .body("message", equalTo("User does not exist"));
    }

    @Test
    public void memberDeletesUserReturnsUnauthorized() {
        userRepository.save(new User("admin", passwordEncoder.encode("password"), Role.ADMIN));
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .header("Authorization", Util.getToken("member", "password", port))
                .delete("http://localhost:" + port + "/api/v1/users/admin")
                .then()
                .statusCode(401);
    }

    @Test
    public void unauthorizedUserDeletesUserReturnsUnauthorized() {
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .delete("http://localhost:" + port + "/api/v1/users/member")
                .then()
                .statusCode(401);
    }

    @Test
    public void unauthorizedUserDeletesItselfReturnsUnauthorized() {
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .delete("http://localhost:" + port + "/api/v1/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void memberDeletesItselfReturnsOk() {
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .header("Authorization", Util.getToken("member", "password", port))
                .delete("http://localhost:" + port + "/api/v1/users")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted user"));

        Assertions.assertTrue(userRepository.findById("member").isEmpty());
    }

    @Test
    public void adminDeletesItselfReturnsOk() {
        userRepository.save(new User("admin", passwordEncoder.encode("password"), Role.ADMIN));

        RestAssured.given()
                .header("Authorization", Util.getToken("admin", "password", port))
                .delete("http://localhost:" + port + "/api/v1/users")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully deleted user"));

        Assertions.assertTrue(userRepository.findById("member").isEmpty());
    }

}
