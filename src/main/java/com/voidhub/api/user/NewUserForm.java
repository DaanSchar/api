package com.voidhub.api.user;

import com.voidhub.api.validation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Getter
public class NewUserForm {

    @NotBlank(message = "Username is required")
    @UsernameConstraint
    private String username;

    @NotBlank(message = "Password is required")
    @PasswordConstraint
    private String password;

}
