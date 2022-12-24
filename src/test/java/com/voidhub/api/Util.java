package com.voidhub.api;

import com.voidhub.api.event.NewEventForm;
import com.voidhub.api.user.Role;
import io.restassured.RestAssured;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    public static String getToken(String username, String password, int port) {
        String body = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        return RestAssured
                .given()
                .contentType("application/json")
                .body(body)
                .post("http://localhost:" + port + "/login")
                .getHeader("Authorization");
    }

    public static String formatDate(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(date);
    }

    public static Date getRandomFutureDate() {
        var cal = Calendar.getInstance();
        Random random = new Random();

        cal.set(
                random.nextInt(1000) + 2000,
                random.nextInt(11) + 1,
                random.nextInt(28) + 1,
                random.nextInt(23),
                random.nextInt(60),
                random.nextInt(60)
        );
        return cal.getTime();
    }

    public static String toBody(NewEventForm newEvent) {
        return "{" +
                "\"title\": \"" + newEvent.getTitle() + "\", " +
                "\"shortDescription\": \"" + newEvent.getShortDescription() + "\", " +
                "\"fullDescription\": \"" + newEvent.getFullDescription() + "\", " +
                "\"applicationDeadline\": \"" + Util.formatDate(newEvent.getApplicationDeadline()) + "\", " +
                "\"startingDate\": \"" + Util.formatDate(newEvent.getStartingDate()) + "\"" +
                "}";
    }

    public static boolean roleWithAuthorityExists(String authority) {
        return getRolesWithAuthority(authority).size() > 0;
    }

    public static List<Role> getRolesWithAuthority(String authority) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getAuthorities().contains(new SimpleGrantedAuthority(authority)))
                .toList();
    }

    public static List<Role> getRolesWithoutAuthority(String authority) {
        return Arrays.stream(Role.values())
                .filter(role -> !role.getAuthorities().contains(new SimpleGrantedAuthority(authority)))
                .toList();
    }

}
