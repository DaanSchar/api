package com.voidhub.api.validation;

import com.voidhub.api.util.validation.constraint.PasswordConstraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PasswordConstraintTest {

    @Autowired
    private Validator validator;

    private final List<String> validPasswords = List.of(
            "P@ssw0rd1",
            "Valid!Password123",
            "valid_passworD4!",
            "$MyBig42inchBooty$"
    );

    private final List<String> invalidPasswords = List.of(
            "invalidpassword",
            "invalid_password",
            "invalid_invalid_invalid_invalid_invalid_invalid",
            "woah",
            "inv",
            "Woah123ThisPasswordIsInvalid",
            ""
    );

    @Test
    public void validUsernames() {
        for (String validPassword : validPasswords) {
            Assertions.assertTrue(
                    getViolations(new Password(validPassword)).isEmpty()
            );
        }
    }

    @Test
    public void invalidUsernames() {
        for (String invalidPassword : invalidPasswords) {
            Assertions.assertFalse(
                    getViolations(new Password(invalidPassword)).isEmpty()
            );
        }
    }

    private Set<ConstraintViolation<Password>> getViolations(Password password) {
        return validator.validate(password);
    }

    private record Password(@PasswordConstraint String value) { }

}
