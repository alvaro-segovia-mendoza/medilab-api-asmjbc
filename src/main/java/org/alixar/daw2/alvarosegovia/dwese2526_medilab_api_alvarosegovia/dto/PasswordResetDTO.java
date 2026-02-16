package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetDTO {
    @NotBlank
    private String token;


    @NotBlank
    @Size(min = 8, max = 72) // 72 es típico para bcrypt
    private String newPassword;


    @NotBlank
    private String confirmPassword;
}

