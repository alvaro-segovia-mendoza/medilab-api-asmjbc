package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado en el dominio clinico para transportar datos de RegistroClinicoReviewDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroClinicoReviewDTO {

    private String observacionesMedico;

    private String recetaOSolucion;

    @NotNull(message = "{validation.clinical.registroClinico.aprobado.required}")
    private Boolean aprobado;
}
