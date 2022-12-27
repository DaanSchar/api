package com.voidhub.api.util.validation.validator;

import com.voidhub.api.util.validation.constraint.DiscordNameConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiscordNameValidator implements ConstraintValidator<DiscordNameConstraint, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && password.matches("^.{3,32}#[0-9]{4}$");
    }

}
