package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado en el dominio clinico para transportar datos de RegistroClinicoCreateDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroClinicoCreateDTO {

    @NotNull(message = "{validation.clinical.registroClinico.citaId.required}")
    private Long citaId;

    private Long tecnicoId;

    @NotBlank(message = "{validation.clinical.registroClinico.resultado.required}")
    private String resultado;

    private String observacionesTecnico;
}
