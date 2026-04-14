package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos necesarios para crear una parada operativa de una ruta.")
public class ParadaCreateDTO {
    @Schema(description = "Ruta a la que pertenece la parada.", example = "1")
    @NotNull(message = "{msg.parada.rutaId.notNull}")
    private Long rutaId;

    @Schema(description = "Nombre visible y real de la ubicacion.", example = "Centro de Salud de Castilleja de la Cuesta")
    @NotBlank(message = "{msg.parada.nombre.notBlank}")
    @Size(max = 100, message = "{msg.parada.nombre.size}")
    private String nombre;

    @Schema(description = "Municipio de la parada.", example = "Castilleja de la Cuesta")
    @NotBlank(message = "{msg.parada.municipio.notBlank}")
    @Size(max = 100, message = "{msg.parada.municipio.size}")
    private String municipio;

    @Schema(description = "Provincia de la parada.", example = "Sevilla")
    @NotBlank(message = "{msg.parada.provincia.notBlank}")
    @Size(max = 100, message = "{msg.parada.provincia.size}")
    private String provincia;

    @Size(max = 150, message = "{msg.parada.direccion.size}")
    private String direccion;

    @NotNull(message = "{msg.parada.orden.notNull}")
    private Integer ordenParada;

    @NotNull(message = "{msg.parada.fecha.notNull}")
    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    @NotNull(message = "{msg.parada.capacidadMaxima.notNull}")
    @Positive(message = "{msg.parada.capacidadMaxima.positive}")
    private Integer capacidadMaxima;

    private Boolean activa;
}
