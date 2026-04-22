package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaDTO {
    private Long id;
    private String nombre;
    private String origen;
    private String destino;
    private boolean activa;
    private Long trailerId;
    private String trailerNombre;
}
