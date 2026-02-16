package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDetailDTO {

    private Long id;
    private String codigo;
    private LocalDateTime fechaHora;
    private String tipoPrueba;
    private Cita.EstadoCita estadoCita;
    private TecnicoDTO tecnico;
}
