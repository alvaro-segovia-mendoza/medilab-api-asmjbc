package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaCreateDTO {
    @NotBlank(message = "{msg.ruta.nombre.notBlank}")
    @Size(max = 100, message = "{msg.ruta.nombre.size}")
    private String nombre;

    @NotBlank(message = "{msg.ruta.origen.notBlank}")
    @Size(max = 100, message = "{msg.ruta.origen.size}")
    private String origen;

    @NotBlank(message = "{msg.ruta.destino.notBlank}")
    @Size(max = 100, message = "{msg.ruta.destino.size}")
    private String destino;

    private Boolean activa;

    @NotNull(message = "{msg.ruta.trailerId.notNull}")
    private Long trailerId;
}
