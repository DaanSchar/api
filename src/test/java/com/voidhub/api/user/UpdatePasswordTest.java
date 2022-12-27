package com.voidhub.api.user;

import com.voidhub.api.BaseTest;
import com.voidhub.api.util.TestUser;
import com.voidhub.api.util.UserUtil;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.Matchers.*;


public class UpdatePasswordTest extends BaseTest {

    private @Autowired UserRepository userRepository;
    private @Autowired UserUtil userUtil;
    private @Autowired PasswordEncoder passwordEncoder;

    @Test
    public void adminUpdatesOwnPasswordReturnsOk() {
        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {
            RestAssured.given()
                    .contentType("application/json")
                    .header(user.getAuthHeader())
                    .body("{\"oldPassword\": \"" + user.unEncodedPassword()+ "\", \"newPassword\": \"newValid!Password22\"}")
                    .when()
                    .put("/api/v1/users/password")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("Successfully updated password"));

            Assertions.assertTrue(passwordEncoder.matches(
                    "newValid!Password22",
                    userRepository.findById(user.getUsername()).get().getPassword()
            ));
        }
    }

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
        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {

            RestAssured.given()
                    .contentType("application/json")
                    .header(user.getAuthHeader())
                    .body("{\"oldPassword\": \"wrong_old_password\", \"newPassword\": \"newValid!Password22\"}")
                    .when()
                    .put("/api/v1/users/password")
                    .then()
                    .statusCode(401)
                    .body("message", equalTo("Wrong password"));

            Assertions.assertTrue(passwordEncoder.matches(
                    user.unEncodedPassword(),
                    userRepository.findById(user.getUsername()).get().getPassword()
            ));
        }
    }

    @Test
    public void userUpdatePasswordWithInvalidNewPasswordReturns422() {
        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {
            RestAssured.given()
                    .contentType("application/json")
                    .header(user.getAuthHeader())
                    .body("{\"oldPassword\": \"" + user.unEncodedPassword() + "\", \"newPassword\": \"ww\"}")
                    .when()
                    .put("/api/v1/users/password")
                    .then()
                    .statusCode(422)
                    .body("message", equalTo("Invalid request"))
                    .body("errors", hasKey("newPassword"));
        }
    }

    @Test
    public void userUpdatesPasswordWithInvalidBodyReturns422() {
        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {
            RestAssured.given()
                    .contentType("application/json")
                    .header(user.getAuthHeader())
                    .body("{\"oldPassword\": \"" + user.unEncodedPassword()+ "\"}")
                    .when()
                    .put("/api/v1/users/password")
                    .then()
                    .statusCode(422)
                    .body("message", equalTo("Invalid request"))
                    .body("errors", hasKey("newPassword"));

            RestAssured.given()
                    .contentType("application/json")
                    .header(user.getAuthHeader())
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

}
