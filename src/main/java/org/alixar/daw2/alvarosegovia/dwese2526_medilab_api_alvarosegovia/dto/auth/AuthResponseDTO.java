package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth;

import lombok.Data;

/**
 * DTO usado en el flujo de autenticacion y credenciales: AuthResponseDTO.
 */
@Data
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private long refreshExpiresIn;
    private String message;

    public AuthResponseDTO(String token, String message) {
        this.token = token;
        this.message = message;
        this.tokenType = "Bearer";
    }

    public AuthResponseDTO(String token,
                           String refreshToken,
                           String tokenType,
                           long expiresIn,
                           long refreshExpiresIn,
                           String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
        this.message = message;
    }
}
