package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialClinicoDTO {

    private Long pacienteId;
    private UserBasicDTO paciente;
    private Integer totalRegistros;
    private LocalDateTime ultimaActualizacion;
    private List<RegistroClinicoDTO> registros;
}
