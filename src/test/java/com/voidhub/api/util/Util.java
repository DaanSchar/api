package com.voidhub.api.util;

import com.voidhub.api.form.EventApplicationForm;
import com.voidhub.api.form.create.CreateEventForm;
import com.voidhub.api.entity.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

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
                random.nextInt(100) + 2030,
                random.nextInt(11) + 1,
                random.nextInt(28) + 1,
                random.nextInt(23),
                random.nextInt(60),
                random.nextInt(60)
        );
        return cal.getTime();
    }

    public static String toBody(CreateEventForm newEvent) {
        return "{" +
                "\"title\": \"" + newEvent.getTitle() + "\", " +
                "\"shortDescription\": \"" + newEvent.getShortDescription() + "\", " +
                "\"fullDescription\": \"" + newEvent.getFullDescription() + "\", " +
                "\"applicationDeadline\": \"" + Util.formatDate(newEvent.getApplicationDeadline()) + "\", " +
                "\"startingDate\": \"" + Util.formatDate(newEvent.getStartingDate()) + "\", " +
                "\"imageId\": \"" + newEvent.getImageId() + "\"" +
                "}";
    }

    public static String toBody(EventApplicationForm form) {
        return "{" +
                "\"email\": \"" + form.getEmail() + "\", " +
                "\"discordName\": \"" + form.getDiscordName() + "\", " +
                "\"minecraftName\": \"" + form.getDiscordName() + "\"" +
                "}";
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
