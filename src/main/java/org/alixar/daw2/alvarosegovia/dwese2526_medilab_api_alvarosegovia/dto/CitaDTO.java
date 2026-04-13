package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

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
public class CitaDTO {

    private Long id;
    private LocalDateTime fechaHora;
    private String tipoPrueba;
    private Cita.EstadoCita estadoCita;

    private String pacienteNombre;
    private String tecnicoNombre;
    private String doctorNombre;
    private String paradaNombre;
    private String municipioParada;
    private String rutaNombre;
    private String trailerNombre;
}
