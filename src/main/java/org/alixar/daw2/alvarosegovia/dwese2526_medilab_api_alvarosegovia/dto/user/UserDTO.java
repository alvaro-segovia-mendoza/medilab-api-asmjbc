package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object (DTO) para la entidad User.
 * Se utiliza para transferir información de usuario entre capas,
 * sin exponer directamente la entidad ni datos sensibles innecesarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String email;
    // Indicadores de estado de la cuenta
    private Boolean active;
    private Boolean accountNonLocked;
    private Boolean emailVerified;
    private Boolean mustChangePassword;

    // Fechas de control de la contraseña
    private LocalDateTime lastPasswordChange;
    private LocalDateTime passwordExpiresAt;

    // Número de intentos fallidos de inicio de sesión
    private Integer failedLoginAttempts;

    // Set de roles que se asignarán al usuario
    private Set<String> roles;

}
