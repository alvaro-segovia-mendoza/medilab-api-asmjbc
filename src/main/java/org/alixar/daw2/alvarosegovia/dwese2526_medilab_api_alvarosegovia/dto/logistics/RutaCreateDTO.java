package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "{validation.logistics.ruta.nombre.required}")
    @Size(max = 100, message = "{validation.logistics.ruta.nombre.size}")
    private String nombre;

    @Size(max = 100, message = "{validation.logistics.ruta.origen.size}")
    private String origen;

    @Size(max = 100, message = "{validation.logistics.ruta.destino.size}")
    private String destino;

    private Boolean activa;

    private Long trailerId;
}
