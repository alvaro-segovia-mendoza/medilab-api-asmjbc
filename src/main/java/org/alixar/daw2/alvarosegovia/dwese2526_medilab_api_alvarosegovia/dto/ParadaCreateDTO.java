package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParadaCreateDTO {
    @NotNull(message = "{msg.parada.rutaId.notNull}")
    private Long rutaId;

    @NotBlank(message = "{msg.parada.nombre.notBlank}")
    @Size(max = 100, message = "{msg.parada.nombre.size}")
    private String nombre;

    @NotBlank(message = "{msg.parada.municipio.notBlank}")
    @Size(max = 100, message = "{msg.parada.municipio.size}")
    private String municipio;

    @Size(max = 150, message = "{msg.parada.direccion.size}")
    private String direccion;

    @NotNull(message = "{msg.parada.orden.notNull}")
    private Integer ordenParada;

    @NotNull(message = "{msg.parada.fecha.notNull}")
    private LocalDate fecha;

    @NotNull(message = "{msg.parada.horaInicio.notNull}")
    private LocalTime horaInicio;

    @NotNull(message = "{msg.parada.horaFin.notNull}")
    private LocalTime horaFin;

    @NotNull(message = "{msg.parada.capacidadMaxima.notNull}")
    @Positive(message = "{msg.parada.capacidadMaxima.positive}")
    private Integer capacidadMaxima;

    private Boolean activa;
}
