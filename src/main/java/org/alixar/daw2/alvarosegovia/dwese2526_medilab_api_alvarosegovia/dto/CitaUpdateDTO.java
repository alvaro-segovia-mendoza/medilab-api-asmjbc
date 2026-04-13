package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaUpdateDTO {

    @NotNull(message = "{msg.cita.id.notEmpty}")
    private Long id;

    @NotNull(message = "{msg.cita.fechaHora.notEmpty}")
    private LocalDateTime fechaHora;

    @NotBlank(message = "{msg.cita.tipoPrueba.notEmpty}")
    @Size(max = 50, message = "{msg.cita.tipoPrueba.size}")
    private String tipoPrueba;

    @NotNull(message = "{msg.cita.estado.notEmpty}")
    private Cita.EstadoCita estadoCita;

    @NotNull(message = "{msg.cita.paciente.notEmpty}")
    private Long pacienteId;

    @NotNull(message = "{msg.cita.parada.notEmpty}")
    private Long paradaId;

    private Long tecnicoId;

    private Long doctorId;
}
