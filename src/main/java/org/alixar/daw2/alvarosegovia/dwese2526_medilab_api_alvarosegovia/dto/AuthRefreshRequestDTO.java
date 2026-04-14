package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRefreshRequestDTO {

    @NotBlank
    private String refreshToken;
}
