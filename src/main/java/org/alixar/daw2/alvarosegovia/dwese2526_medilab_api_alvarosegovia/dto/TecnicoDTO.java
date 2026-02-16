package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO básico para mostrar técnicos en listados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TecnicoDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private boolean activo;
    private String dni;
    private String fechaNacimiento;
    private String direccion;
    private String localidad;
    private String provincia;
}
