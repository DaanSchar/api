package com.voidhub.api.user;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdatePasswordForm {

    @NotBlank
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{6,20}$",
            message = "Password must contain at least one digit, " +
                    "one lowercase letter," +
                    " one uppercase letter," +
                    " one special character" +
                    " and must be between 6 and 20 characters"
    )
    private String newPassword;
}
