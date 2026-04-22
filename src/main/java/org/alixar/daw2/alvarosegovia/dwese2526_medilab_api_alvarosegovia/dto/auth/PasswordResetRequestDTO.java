package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    @NotBlank(message = "{validation.auth.email.required}")
    @Email(message = "{validation.auth.email.invalid}")
    private String email;
}
