package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar un paciente con email y confirmación de contraseña.")
public class RegisterRequestDTO {

    @NotBlank
    @Email
    @Size(max = 100)
    @Schema(description = "Correo electrónico del usuario.", example = "paciente@medilab.es")
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    @Schema(description = "Contraseña en texto plano. Debe tener entre 8 y 72 caracteres.", example = "Paciente123!")
    private String password;

    @NotBlank
    @Schema(description = "Confirmación de la contraseña. Debe coincidir con el campo password.", example = "Paciente123!")
    private String confirmPassword;
}
