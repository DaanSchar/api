package com.voidhub.api.validation;

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
public class UsernameConstraintTest {

    @Autowired
    private Validator validator;

    private final List<String> validUsernames = List.of(
            "user",
            "user1",
            "user_1",
            "user-1",
            "user.1",
            "vallliiiid___user",
            "UsErNaM._123"
    );
    private final List<String> invalidUsernames = List.of(
            "in",
            "invalidinvalidinvalidinvalidinvalid",
            "invalid user",
            "i am_not_valid",
            "invalid@user",
            "invalid#user8",
            "invalid$user",
            "invalid%user",
            "abc"
    );

    @Test
    public void validUsernames() {
        for (String validUsername : validUsernames) {
            Assertions.assertTrue(
                    getViolations(new Username(validUsername)).isEmpty()
            );
        }
    }

    @Test
    public void invalidUsernames() {
        for (String validUsername : invalidUsernames) {
            Assertions.assertFalse(
                    getViolations(new Username(validUsername)).isEmpty()
            );
        }
    }

    private Set<ConstraintViolation<Username>> getViolations(Username username) {
        return validator.validate(username);
    }

    private record Username(@UsernameConstraint String value) {

    }

}
