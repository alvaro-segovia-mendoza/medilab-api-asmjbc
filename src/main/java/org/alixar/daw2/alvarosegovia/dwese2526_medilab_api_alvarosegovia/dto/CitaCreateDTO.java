package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

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
    @NotBlank(message = "{msg.cita.tipoPrueba.notEmpty}")
    @Size(max = 50, message = "{msg.cita.tipoPrueba.size}")
    private String tipoPrueba;

    @Schema(description = "Paciente que reserva la cita.", example = "5")
    @NotNull(message = "{msg.cita.paciente.notEmpty}")
    private Long pacienteId;

    @Schema(description = "Identificador del slot reservable.", example = "5")
    @NotNull(message = "{msg.cita.slot.notEmpty}")
    private Long slotId;
}
