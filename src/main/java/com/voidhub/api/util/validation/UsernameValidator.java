package com.voidhub.api.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null
                && username.matches("^[a-zA-Z0-9._-]{4,20}$");
    }

}
