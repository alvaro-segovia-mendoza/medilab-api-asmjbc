package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado en el dominio logistico para transportar datos de TrailerUpdateDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrailerUpdateDTO {
    @NotNull(message = "{validation.logistics.trailer.id.required}")
    private Long id;

    @NotBlank(message = "{validation.logistics.trailer.codigo.required}")
    @Size(max = 30, message = "{validation.logistics.trailer.codigo.size}")
    private String codigo;

    @NotBlank(message = "{validation.logistics.trailer.nombre.required}")
    @Size(max = 100, message = "{validation.logistics.trailer.nombre.size}")
    private String nombre;

    @NotNull(message = "{validation.logistics.trailer.activo.required}")
    private Boolean activo;

    @Size(max = 255, message = "{validation.logistics.trailer.descripcion.size}")
    private String descripcion;
}
