package com.voidhub.api.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NewUserForm {

    @NotBlank(message = "Username is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]{4,20}$",
            message = "Username must contain only letters, numbers, dots, underscores, dashes" +
                    "and be between 4 and 20 characters long"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{6,20}$",
            message = "Password must contain at least one digit, " +
                    "one lowercase letter," +
                    " one uppercase letter," +
                    " one special character" +
                    " and must be between 6 and 20 characters"
    )
    private String password;

}
