package com.voidhub.api.form.update;

import com.voidhub.api.util.validation.constraint.PasswordConstraint;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@Getter
public class UpdatePasswordForm {

    @NotBlank
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @PasswordConstraint
    private String newPassword;
}
