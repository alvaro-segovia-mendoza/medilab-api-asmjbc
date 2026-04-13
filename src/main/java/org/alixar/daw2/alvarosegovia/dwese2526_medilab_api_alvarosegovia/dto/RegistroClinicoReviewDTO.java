package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroClinicoReviewDTO {

    private String observacionesMedico;

    private String recetaOSolucion;

    @NotNull(message = "{msg.registroClinico.aprobado.notnull}")
    private Boolean aprobado;
}
