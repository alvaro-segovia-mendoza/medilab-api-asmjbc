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
public class CitaCreateDTO {

    // Siempre null en creación
    private Long id;

    @NotBlank(message = "{msg.cita.codigo.notEmpty}")
    @Size(max = 50, message = "{msg.cita.codigo.size}")
    private String codigo;

    @NotNull(message = "{msg.cita.fechaHora.notEmpty}")
    private LocalDateTime fechaHora;

    @NotBlank(message = "{msg.cita.tipoPrueba.notEmpty}")
    @Size(max = 50, message = "{msg.cita.tipoPrueba.size}")
    private String tipoPrueba;


    @NotNull(message = "{msg.cita.estadoCita.notEmpty}")
    private Cita.EstadoCita estadoCita;

    @NotNull(message = "{msg.cita.tecnico.notEmpty}")
    private Long tecnicoId; // Para mostrar a qué técnico pertenece
}
