package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO usado en el dominio logistico para transportar datos de ParadaBasicDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParadaBasicDTO {
    private Long id;
    private String nombre;
    private String municipio;
    private String provincia;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer capacidadMaxima;
    private String rutaNombre;
    private String trailerNombre;
}
