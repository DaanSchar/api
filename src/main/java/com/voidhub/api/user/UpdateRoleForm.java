package com.voidhub.api.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRoleForm {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Role is mandatory")
    private String role;
}
