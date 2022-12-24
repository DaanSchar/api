package com.voidhub.api.user;

import com.voidhub.api.validation.PasswordConstraint;
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
