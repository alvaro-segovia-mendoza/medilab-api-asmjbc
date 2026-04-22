package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO para actualizar usuarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @NotNull(message = "{validation.user.account.id.required}")
    private Long id;


    @Email(message = "{validation.user.account.email.invalid}")
    @NotBlank(message = "{validation.user.account.email.required}")
    @Size(min = 4, max = 100, message = "{validation.user.account.email.size}")
    private String email;


    @NotNull(message = "{validation.user.account.active.required}")
    private Boolean active;


    @NotNull(message = "{validation.user.account.accountNonLocked.required}")
    private Boolean accountNonLocked;


    @PastOrPresent(message = "{validation.user.account.lastPasswordChange.pastOrPresent}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime lastPasswordChange;


    @FutureOrPresent(message = "{validation.user.account.passwordExpiresAt.futureOrPresent}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime passwordExpiresAt;


    @Min(value = 0, message = "{validation.user.account.failedLoginAttempts.min}")
    private Integer failedLoginAttempts;


    @NotNull(message = "{validation.user.account.emailVerified.required}")
    private Boolean emailVerified;


    @NotNull(message = "{validation.user.account.mustChangePassword.required}")
    private Boolean mustChangePassword;


    // ─────────────────────────────────────
    // Roles seleccionados (ids de Role) - OBLIGATORIOS
    // ─────────────────────────────────────
    @NotEmpty(message = "{validation.user.account.roleIds.required}")
    private Set<Long> roleIds = new HashSet<>();
}
