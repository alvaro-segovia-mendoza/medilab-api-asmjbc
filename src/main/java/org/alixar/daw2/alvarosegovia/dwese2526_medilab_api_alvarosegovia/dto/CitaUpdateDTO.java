package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos editables de una cita existente.")
public class CitaUpdateDTO {

    @Schema(description = "Identificador de la cita.", example = "1")
    @NotNull(message = "{msg.cita.id.notEmpty}")
    private Long id;

    @Schema(description = "Tipo de prueba solicitado.", example = "Radiografia")
    @NotBlank(message = "{msg.cita.tipoPrueba.notEmpty}")
    @Size(max = 50, message = "{msg.cita.tipoPrueba.size}")
    private String tipoPrueba;

    @Schema(description = "Estado actual de la cita.", example = "CONFIRMADA")
    @NotNull(message = "{msg.cita.estado.notEmpty}")
    private Cita.EstadoCita estadoCita;

    @Schema(description = "Paciente asociado a la cita.", example = "5")
    @NotNull(message = "{msg.cita.paciente.notEmpty}")
    private Long pacienteId;

    @Schema(description = "Slot reservado por la cita.", example = "7")
    @NotNull(message = "{msg.cita.slot.notEmpty}")
    private Long slotId;

    @Schema(description = "Tecnico asignado a la cita.", example = "6")
    private Long tecnicoId;

    @Schema(description = "Medico revisor asignado a la cita.", example = "3")
    private Long doctorId;
}
