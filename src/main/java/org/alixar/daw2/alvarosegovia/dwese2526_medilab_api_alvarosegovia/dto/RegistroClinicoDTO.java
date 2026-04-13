package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.RegistroClinico;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroClinicoDTO {

    private Long id;
    private Long citaId;
    private String tipoPrueba;
    private String resultado;
    private String observacionesTecnico;
    private String observacionesMedico;
    private String recetaOSolucion;
    private RegistroClinico.EstadoRegistroClinico estado;
    private UserBasicDTO paciente;
    private UserBasicDTO tecnico;
    private UserBasicDTO medico;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
