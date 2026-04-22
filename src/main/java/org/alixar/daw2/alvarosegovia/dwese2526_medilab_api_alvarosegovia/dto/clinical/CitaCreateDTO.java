package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de reserva de una cita sobre un slot concreto.")
public class CitaCreateDTO {

    @Schema(description = "Tipo de prueba solicitada.", example = "Analitica")
    @NotBlank(message = "{validation.clinical.cita.tipoPrueba.required}")
    @Size(max = 50, message = "{validation.clinical.cita.tipoPrueba.size}")
    private String tipoPrueba;

    @Schema(description = "Paciente que reserva la cita.", example = "5")
    @NotNull(message = "{validation.clinical.cita.pacienteId.required}")
    private Long pacienteId;

    @Schema(description = "Identificador del slot reservable.", example = "5")
    @NotNull(message = "{validation.clinical.cita.slotId.required}")
    private Long slotId;
}
