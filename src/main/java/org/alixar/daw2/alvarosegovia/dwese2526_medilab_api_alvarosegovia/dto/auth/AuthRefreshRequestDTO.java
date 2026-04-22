package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRefreshRequestDTO {

    @NotBlank(message = "{validation.auth.refreshToken.required}")
    private String refreshToken;
}
