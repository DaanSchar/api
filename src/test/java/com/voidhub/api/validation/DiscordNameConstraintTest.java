package com.voidhub.api.validation;

import com.voidhub.api.util.validation.constraint.DiscordNameConstraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DiscordNameConstraintTest {

    @Autowired
    private Validator validator;

    private final List<String> validDiscordNames = List.of(
            "user#0000",
            "daan#0413",
            "BigUser_123#1234",
            "user_1#1234",
            "Disコルド#0001",
            "KiBender#1234",
            "SkankHunt42#2134"

    );

    private final List<String> invalidDiscordNames = List.of(
            "user#111",
            "daan#15",
            "invalidUser",
            "KiBender#1",
            "SkankHunt42#12345",
            "BigBoy#223f"
    );

    @Test
    public void validDiscordNames() {
        for (String validDiscordName : validDiscordNames) {
            Assertions.assertTrue(getViolations(new DiscordName(validDiscordName)).isEmpty());
        }
    }

    @Test
    public void invalidUsernames() {
        for (String validUsername : invalidDiscordNames) {
            Assertions.assertFalse(getViolations(new DiscordName(validUsername)).isEmpty());
        }
    }

    private Set<ConstraintViolation<DiscordName>> getViolations(DiscordName discordName) {
        return validator.validate(discordName);
    }

    private record DiscordName(@DiscordNameConstraint String value) {
    }
}
