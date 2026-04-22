package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDetailDTO {

    private Long id;
    private Long slotId;
    private LocalDateTime fechaHora;
    private String tipoPrueba;
    private Cita.EstadoCita estadoCita;

    private UserBasicDTO paciente;
    private UserBasicDTO tecnico;
    private UserBasicDTO doctor;
    private ParadaBasicDTO parada;
    private String rutaNombre;
    private String trailerNombre;
}
