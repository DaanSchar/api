package com.voidhub.api.user;

import com.voidhub.api.util.Util;
import com.voidhub.api.entity.Role;
import com.voidhub.api.entity.User;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UpdatePasswordTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void adminUpdatesOwnPasswordReturnsOk() {
        userRepository.save(new User("admin", passwordEncoder.encode("password"), Role.ADMIN));

        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", Util.getToken("admin", "password", port))
                .body("{\"oldPassword\": \"password\", \"newPassword\": \"newValid!Password22\"}")
                .when()
                .put("/api/v1/users/password")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully updated password"));

        Assertions.assertTrue(passwordEncoder.matches(
                "newValid!Password22",
                userRepository.findById("admin").get().getPassword()
        ));    }

    @Test
    public void memberUpdatesOwnPasswordReturnsOk() {
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", Util.getToken("member", "password", port))
                .body("{\"oldPassword\": \"password\", \"newPassword\": \"newValid!Password22\"}")
                .when()
                .put("/api/v1/users/password")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully updated password"));

        Assertions.assertTrue(passwordEncoder.matches(
                "newValid!Password22",
                userRepository.findById("member").get().getPassword()
        ));    }

    @Test
    public void unauthenticatedUserUpdatesPasswordReturnsUnauthorized() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"oldPassword\": \"password\", \"newPassword\": \"newValid!Password22\"}")
                .when()
                .put("/api/v1/users/password")
                .then()
                .statusCode(401);
    }

    @Test
    public void userUpdatesPasswordGivenWrongOldPasswordReturnsUnauthorized() {
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", Util.getToken("member", "password", port))
                .body("{\"oldPassword\": \"wrong_old_password\", \"newPassword\": \"newValid!Password22\"}")
                .when()
                .put("/api/v1/users/password")
                .then()
                .statusCode(401)
                .body("message", equalTo("Wrong password"));

        Assertions.assertTrue(passwordEncoder.matches(
                "password",
                userRepository.findById("member").get().getPassword()
        ));
    }

    @Test
    public void userUpdatePasswordWithInvalidNewPasswordReturns422() {
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", Util.getToken("member", "password", port))
                .body("{\"oldPassword\": \"password\", \"newPassword\": \"ww\"}")
                .when()
                .put("/api/v1/users/password")
                .then()
                .statusCode(422)
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("newPassword"));
    }

    @Test
    public void userUpdatesPasswordWithInvalidBodyReturns422() {
        userRepository.save(new User("member", passwordEncoder.encode("password"), Role.MEMBER));

        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", Util.getToken("member", "password", port))
                .body("{\"oldPassword\": \"password\"}")
                .when()
                .put("/api/v1/users/password")
                .then()
                .statusCode(422)
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("newPassword"));

        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", Util.getToken("member", "password", port))
                .body("{}")
                .when()
                .put("/api/v1/users/password")
                .then()
                .statusCode(422)
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("newPassword"))
                .body("errors", hasKey("oldPassword"));
    }

}
