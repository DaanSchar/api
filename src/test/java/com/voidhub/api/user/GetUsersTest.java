package com.voidhub.api.user;

import com.voidhub.api.util.Util;
import com.voidhub.api.entity.Role;
import com.voidhub.api.entity.User;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GetUsersTest {

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
    public void adminUserRequestsAllUsersReturnsOk() {
        String url = "http://localhost:" + port + "/api/v1/users";

        userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );
        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password2"))
                .username("username2")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .get(url)
                .then()
                .body("size()", equalTo(2))
                .body("[0].username", equalTo("username"))
                .body("[1].username", equalTo("username2"))
                .body("[0].password", equalTo(null))
                .body("[0].password", equalTo(null))
                .statusCode(200);
    }

    @Test
    public void memberRequestsAllUsersReturnsForbidden() {
        String url = "http://localhost:" + port + "/api/v1/users";

        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );
        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password2"))
                .username("username2")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .get(url)
                .then()
                .body("message", equalTo("Access Denied"))
                .statusCode(401);
    }

    @Test
    public void unauthenticatedUserRequestsAllUsersReturnsForbidden() {
        String url = "http://localhost:" + port + "/api/v1/users";

        RestAssured.get(url)
                .then()
                .statusCode(401);
    }

    @Test
    public void adminUserGetsExistingUserReturnsOk() {
        String url = "http://localhost:" + port + "/api/v1/users/username";

        userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .get(url)
                .then()
                .body("username", equalTo("username"))
                .body("password", equalTo(null))
                .body("role", equalTo(null))
                .statusCode(200);
    }

    @Test
    public void memberUserGetsExistingUserReturnsOk() {
        String url = "http://localhost:" + port + "/api/v1/users/username";

        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .get(url)
                .then()
                .body("username", equalTo("username"))
                .body("password", equalTo(null))
                .body("role", equalTo(null))
                .statusCode(200);
    }

    @Test
    public void adminUserGetsNonExistingUserReturnsNotFound() {
        String url = "http://localhost:" + port + "/api/v1/users/nonexisting";

        userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .get(url)
                .then()
                .statusCode(404);
    }

    @Test
    public void memberUserGetsNonExistingUserReturnsNotFound() {
        String url = "http://localhost:" + port + "/api/v1/users/nonexisting";

        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .get(url)
                .then()
                .statusCode(404);
    }

    @Test
    public void unauthenticatedUserGetsExistingUserReturnsForbidden() {
        String url = "http://localhost:" + port + "/api/v1/users/username";

        userRepository.save(User.builder()
                .role(Role.MEMBER)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.get(url)
                .then()
                .statusCode(401);
    }

    @Test
    public void unauthenticatedUserGetsNonExistingUserReturnsForbidden() {
        String url = "http://localhost:" + port + "/api/v1/users/username";

        RestAssured.get(url)
                .then()
                .statusCode(401);
    }

//    private String getToken(String username, String password) {
//        String body = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
//
//        return RestAssured
//                .given()
//                .contentType("application/json")
//                .body(body)
//                .post("http://localhost:" + port + "/login")
//                .getHeader("Authorization");
//    }

}
