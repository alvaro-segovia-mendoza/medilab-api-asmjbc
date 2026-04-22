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
public class TrailerCreateDTO {
    @NotBlank(message = "{validation.logistics.trailer.codigo.required}")
    @Size(max = 30, message = "{validation.logistics.trailer.codigo.size}")
    private String codigo;

    @NotBlank(message = "{validation.logistics.trailer.nombre.required}")
    @Size(max = 100, message = "{validation.logistics.trailer.nombre.size}")
    private String nombre;

    private Boolean activo;

    @Size(max = 255, message = "{validation.logistics.trailer.descripcion.size}")
    private String descripcion;
}
