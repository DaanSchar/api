package com.voidhub.api.form.create;

import com.voidhub.api.util.validation.constraint.DiscordNameConstraint;
import com.voidhub.api.util.validation.constraint.PasswordConstraint;
import com.voidhub.api.util.validation.constraint.UsernameConstraint;
import jakarta.validation.constraints.Email;
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

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Discord name is required")
    @DiscordNameConstraint
    private String discordName;

    @NotBlank(message = "Minecraft name is required")
    private String minecraftName;

}
