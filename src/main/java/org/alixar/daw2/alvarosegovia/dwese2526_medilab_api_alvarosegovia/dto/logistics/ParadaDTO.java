package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO usado en el dominio logistico para transportar datos de ParadaDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParadaDTO {
    private Long id;
    private String nombre;
    private String municipio;
    private String provincia;
    private String direccion;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private Integer ordenParada;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer capacidadMaxima;
    private boolean activa;
    private Long rutaId;
    private String rutaNombre;
    private String trailerNombre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
