package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de detalle completo para técnicos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoDetailDTO {

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
    private List<CitaDTO> citas;
}
