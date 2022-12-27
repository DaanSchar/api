package com.voidhub.api.util.validation.constraint;

import com.voidhub.api.util.validation.validator.DiscordNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DiscordNameValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordNameConstraint {

    String message() default "Discord name must end with a '#' and a 4-digit number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
