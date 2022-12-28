package com.voidhub.api.form;

import com.voidhub.api.util.validation.constraint.DiscordNameConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventApplicationForm {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Discord name is required")
    @DiscordNameConstraint
    private String discordName;

    @NotBlank(message = "Minecraft name is required")
    private String minecraftName;

}
