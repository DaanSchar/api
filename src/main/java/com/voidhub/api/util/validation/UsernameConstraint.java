package com.voidhub.api.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameConstraint {
    String message() default "Username must contain only letters, numbers, dots, underscores, dashes" +
            "and be between 4 and 20 characters long";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
