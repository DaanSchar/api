package com.voidhub.api;

import com.voidhub.api.user.*;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserTestUtil {

    private @Autowired UserRepository userRepository;
    private @Autowired PasswordEncoder passwordEncoder;

    public Pair<User, String> createUserAndLogin(String username, String password, Role role, int port) {
        User user = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build());

        String token = getToken(username, password, port);

        return Pair.of(user, token);
    }

    public static String getToken(String username, String password, int port) {
        String body = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        return RestAssured
                .given()
                .contentType("application/json")
                .body(body)
                .post("http://localhost:" + port + "/login")
                .getHeader("Authorization");
    }
}
