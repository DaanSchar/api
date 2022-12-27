package com.voidhub.api.util;

import com.voidhub.api.entity.Role;
import com.voidhub.api.entity.User;
import com.voidhub.api.repository.UserRepository;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    private @Autowired UserRepository userRepository;
    private @Autowired PasswordEncoder passwordEncoder;

    public List<TestUser> getUsersWithAuthority(String authority, int port) {
        return Util.getRolesWithAuthority(authority)
                .stream()
                .map(role ->
                        createAndSaveTestUser(
                                role.name() + "User",
                                role.name() + "Password",
                                role,
                                port
                        )
                ).collect(Collectors.toList());
    }

    public List<TestUser> getUsersWithoutAuthority(String authority, int port) {
        return Util.getRolesWithoutAuthority(authority)
                .stream()
                .map(role ->
                        createAndSaveTestUser(
                                role.name() + "User",
                                role.name() + "Password",
                                role,
                                port
                        )
                ).collect(Collectors.toList());
    }

    public TestUser getUserWithAuthority(String authority, int port) {
        return getUsersWithAuthority(authority, port).get(0);
    }

    private TestUser createAndSaveTestUser(String username, String password, Role role, int port) {
        User user = userRepository.save(User
                .builder()
                .username(username)
                .role(role)
                .password(passwordEncoder.encode(password))
                .build()
        );

        String token = getToken(username, password, port);
        return new TestUser(user, password, token);
    }

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

    public void clearUsers() {
        userRepository.deleteAll();
    }
}
