package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO de detalle para Usuarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {


    private Long id;


    private String email;


    private boolean active;


    private boolean accountNonLocked;


    private LocalDateTime lastPasswordChange;


    private LocalDateTime passwordExpiresAt;


    private Integer failedLoginAttempts;


    private boolean emailVerified;


    private boolean mustChangePassword;


    // ────────────────────────────────────────────
    // Campos del perfil del usuario (UserProfile)
    // ────────────────────────────────────────────


    private String firstName;


    private String lastName;


    private String phoneNumber;


    private String profileImage;


    private String bio;


    private String locale;


    private String dni;


    private LocalDate dateOfBirth;


    private String address;


    private String city;


    private String province;


    // ────────────────────────────────────────────
    // Roles del usuario (nombres de rol)
    // ────────────────────────────────────────────
    private Set<String> roles;
}
