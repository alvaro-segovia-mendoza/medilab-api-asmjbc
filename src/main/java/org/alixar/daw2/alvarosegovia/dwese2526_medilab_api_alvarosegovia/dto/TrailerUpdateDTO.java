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
public class TrailerUpdateDTO {
    @NotNull(message = "{msg.trailer.id.notNull}")
    private Long id;

    @NotBlank(message = "{msg.trailer.codigo.notBlank}")
    @Size(max = 30, message = "{msg.trailer.codigo.size}")
    private String codigo;

    @NotBlank(message = "{msg.trailer.nombre.notBlank}")
    @Size(max = 100, message = "{msg.trailer.nombre.size}")
    private String nombre;

    @NotNull(message = "{msg.trailer.activo.notNull}")
    private Boolean activo;

    @Size(max = 255, message = "{msg.trailer.descripcion.size}")
    private String descripcion;
}
