package com.voidhub.api.user;

import com.voidhub.api.Util;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UpdateUserRoleTest {

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
    public void adminUpdatesUserRoleReturnsOk() {
        String url = "http://localhost:" + port + "/api/v1/users/role";

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
                .body("{\"username\": \"username2\", \"role\": \"ADMIN\"}")
                .contentType("application/json")
                .put(url)
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully updated user role"));

        User user = userRepository.findById("username2").get();
        Assertions.assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    public void adminUpdatesUserWithDuplicateRoleRoleReturnsBadRequest() {
        String url = "http://localhost:" + port + "/api/v1/users/role";

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
                .body("{\"username\": \"username2\", \"role\": \"MEMBER\"}")
                .contentType("application/json")
                .put(url)
                .then()
                .statusCode(409)
                .body("message", equalTo("User already has assigned role"));

        User user = userRepository.findById("username2").get();
        Assertions.assertEquals(Role.MEMBER, user.getRole());
    }

    @Test
    public void adminUpdatesUserRoleWithInvalidRoleReturnsBadRequest() {
        String url = "http://localhost:" + port + "/api/v1/users/role";

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
                .body("{\"username\": \"username2\", \"role\": \"INVALID\"}")
                .contentType("application/json")
                .put(url)
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid role"));
    }

    @Test
    public void adminUpdatesUserRoleWithNonExistingUsernameReturnsBadRequest() {
        String url = "http://localhost:" + port + "/api/v1/users/role";

        userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .body("{\"username\": \"username3\", \"role\": \"ADMIN\"}")
                .contentType("application/json")
                .put(url)
                .then()
                .statusCode(404)
                .body("message", equalTo("User does not exist"));
    }

    @Test
    public void adminUpdatesUserRoleWithInvalidBodyReturns422() {
        String url = "http://localhost:" + port + "/api/v1/users/role";

        userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .body("{\"username\": \"username3\"}")
                .contentType("application/json")
                .put(url)
                .then()
                .statusCode(422)
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("role"));
    }

    @Test
    public void adminUpdatesUserRoleWithEmptyBodyReturns422() {
        String url = "http://localhost:" + port + "/api/v1/users/role";

        userRepository.save(User.builder()
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("password"))
                .username("username")
                .build()
        );

        RestAssured.given()
                .header("Authorization", Util.getToken("username", "password", port))
                .body("{}")
                .contentType("application/json")
                .put(url)
                .then()
                .statusCode(422)
                .body("message", equalTo("Invalid request"))
                .body("errors", hasKey("username"))
                .body("errors", hasKey("role"));
    }

    @Test
    public void memberUpdatesUserRoleReturnsUnauthorized() {
        String url = "http://localhost:" + port + "/api/v1/users/role";

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
                .body("{\"username\": \"username2\", \"role\": \"ADMIN\"}")
                .contentType("application/json")
                .put(url)
                .then()
                .statusCode(401);
    }

}
