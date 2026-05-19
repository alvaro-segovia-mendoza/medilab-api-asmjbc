package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO usado en el dominio logistico para transportar datos de RutaReservableDTO.
 */
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
