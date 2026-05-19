package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO usado en el dominio logistico para transportar datos de RutaDTO.
 */
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
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String descripcion;
    private Long trailerId;
    private String trailerNombre;
    private List<Long> tecnicoIds;
    private List<UserBasicDTO> tecnicos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
