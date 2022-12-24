package com.voidhub.api.form.create;

import com.voidhub.api.util.validation.PasswordConstraint;
import com.voidhub.api.util.validation.UsernameConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Getter
public class CreateUserForm {

    @NotBlank(message = "Username is required")
    @UsernameConstraint
    private String username;

    @NotBlank(message = "Password is required")
    @PasswordConstraint
    private String password;

}
