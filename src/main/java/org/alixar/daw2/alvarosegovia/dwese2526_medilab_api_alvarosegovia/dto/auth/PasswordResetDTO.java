package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetDTO {
    @NotBlank(message = "{validation.auth.reset.token.required}")
    private String token;


    @NotBlank(message = "{validation.auth.reset.newPassword.required}")
    @Size(min = 8, max = 72, message = "{validation.auth.password.size}")
    private String newPassword;


    @NotBlank(message = "{validation.auth.reset.confirmPassword.required}")
    private String confirmPassword;
}
