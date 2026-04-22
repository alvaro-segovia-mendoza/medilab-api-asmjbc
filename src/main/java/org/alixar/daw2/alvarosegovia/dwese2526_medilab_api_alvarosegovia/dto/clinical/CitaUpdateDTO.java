package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical;

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
    @NotNull(message = "{validation.clinical.cita.id.required}")
    private Long id;

    @Schema(description = "Tipo de prueba solicitado.", example = "Radiografia")
    @NotBlank(message = "{validation.clinical.cita.tipoPrueba.required}")
    @Size(max = 50, message = "{validation.clinical.cita.tipoPrueba.size}")
    private String tipoPrueba;

    @Schema(description = "Estado actual de la cita.", example = "CONFIRMADA")
    @NotNull(message = "{validation.clinical.cita.estadoCita.required}")
    private Cita.EstadoCita estadoCita;

    @Schema(description = "Paciente asociado a la cita.", example = "5")
    @NotNull(message = "{validation.clinical.cita.pacienteId.required}")
    private Long pacienteId;

    @Schema(description = "Slot reservado por la cita.", example = "7")
    @NotNull(message = "{validation.clinical.cita.slotId.required}")
    private Long slotId;

    @Schema(description = "Tecnico asignado a la cita.", example = "6")
    private Long tecnicoId;

    @Schema(description = "Medico revisor asignado a la cita.", example = "3")
    private Long doctorId;
}
