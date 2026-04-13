package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaReservableDTO {
    private Long rutaId;
    private String rutaNombre;
    private String origen;
    private String destino;
    private Long trailerId;
    private String trailerNombre;
    private List<DisponibilidadParadaDTO> paradas;
}
