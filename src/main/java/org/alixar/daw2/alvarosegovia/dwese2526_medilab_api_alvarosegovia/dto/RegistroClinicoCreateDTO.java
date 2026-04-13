package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroClinicoCreateDTO {

    @NotNull(message = "{msg.registroClinico.citaId.notnull}")
    private Long citaId;

    private Long tecnicoId;

    @NotBlank(message = "{msg.registroClinico.resultado.notblank}")
    private String resultado;

    private String observacionesTecnico;
}
