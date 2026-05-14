package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaCreateDTO {
    @NotBlank(message = "{validation.logistics.ruta.nombre.required}")
    @Size(max = 100, message = "{validation.logistics.ruta.nombre.size}")
    private String nombre;

    @Size(max = 100, message = "{validation.logistics.ruta.origen.size}")
    private String origen;

    @Size(max = 100, message = "{validation.logistics.ruta.destino.size}")
    private String destino;

    private Boolean activa;

    @NotNull(message = "{validation.logistics.ruta.fechaInicio.required}")
    private LocalDate fechaInicio;

    @NotNull(message = "{validation.logistics.ruta.fechaFin.required}")
    private LocalDate fechaFin;

    @Size(max = 500, message = "{validation.logistics.ruta.descripcion.size}")
    private String descripcion;

    @NotNull(message = "{validation.logistics.ruta.trailerId.required}")
    private Long trailerId;

    @NotNull(message = "{validation.logistics.ruta.tecnicoIds.required}")
    @Size(min = 1, max = 2, message = "{validation.logistics.ruta.tecnicoIds.size}")
    private List<Long> tecnicoIds;
}
