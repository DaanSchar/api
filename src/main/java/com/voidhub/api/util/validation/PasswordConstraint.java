package com.voidhub.api.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraint {

    String message() default "Password must contain at least one digit, " +
            "one lowercase letter," +
            " one uppercase letter," +
            " one special character" +
            " and must be between 6 and 30 characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
