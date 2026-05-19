package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO usado en el dominio logistico para transportar datos de ParadaCreateDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos necesarios para crear una parada operativa de una ruta.")
public class ParadaCreateDTO {
    @Schema(description = "Ruta a la que pertenece la parada.", example = "1")
    @NotNull(message = "{validation.logistics.parada.rutaId.required}")
    private Long rutaId;

    @Schema(description = "Nombre visible y real de la ubicacion.", example = "Centro de Salud de Castilleja de la Cuesta")
    @NotBlank(message = "{validation.logistics.parada.nombre.required}")
    @Size(max = 100, message = "{validation.logistics.parada.nombre.size}")
    private String nombre;

    @Schema(description = "Municipio de la parada.", example = "Castilleja de la Cuesta")
    @NotBlank(message = "{validation.logistics.parada.municipio.required}")
    @Size(max = 100, message = "{validation.logistics.parada.municipio.size}")
    private String municipio;

    @Schema(description = "Provincia de la parada.", example = "Sevilla")
    @NotBlank(message = "{validation.logistics.parada.provincia.required}")
    @Size(max = 100, message = "{validation.logistics.parada.provincia.size}")
    private String provincia;

    @Size(max = 150, message = "{validation.logistics.parada.direccion.size}")
    private String direccion;

    @Schema(description = "Latitud de la parada. Si no se indica, se geocodifica automáticamente desde la dirección.", example = "37.386230")
    @DecimalMin(value = "-90.0", message = "{validation.logistics.parada.latitud.range}")
    @DecimalMax(value = "90.0", message = "{validation.logistics.parada.latitud.range}")
    @Digits(integer = 3, fraction = 6, message = "{validation.logistics.parada.latitud.format}")
    private BigDecimal latitud;

    @Schema(description = "Longitud de la parada. Si no se indica, se geocodifica automáticamente desde la dirección.", example = "-6.051110")
    @DecimalMin(value = "-180.0", message = "{validation.logistics.parada.longitud.range}")
    @DecimalMax(value = "180.0", message = "{validation.logistics.parada.longitud.range}")
    @Digits(integer = 3, fraction = 6, message = "{validation.logistics.parada.longitud.format}")
    private BigDecimal longitud;

    @NotNull(message = "{validation.logistics.parada.ordenParada.required}")
    private Integer ordenParada;

    @NotNull(message = "{validation.logistics.parada.fecha.required}")
    private LocalDate fecha;

    @NotNull(message = "{validation.logistics.parada.horaInicio.required}")
    private LocalTime horaInicio;

    @NotNull(message = "{validation.logistics.parada.horaFin.required}")
    private LocalTime horaFin;

    @NotNull(message = "{validation.logistics.parada.capacidadMaxima.required}")
    @Positive(message = "{validation.logistics.parada.capacidadMaxima.positive}")
    private Integer capacidadMaxima;

    private Boolean activa;
}
