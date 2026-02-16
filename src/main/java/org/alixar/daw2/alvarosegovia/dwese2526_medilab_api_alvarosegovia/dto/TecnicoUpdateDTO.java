package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar técnicos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TecnicoUpdateDTO {

    @NotNull(message = "{msg.tecnico.id.notEmpty}")
    private Long id;

    @NotBlank(message = "{msg.tecnico.nombre.notEmpty}")
    @Size(max = 100, message = "{msg.tecnico.nombre.size}")
    private String nombre;

    @NotBlank(message = "{msg.tecnico.apellidos.notEmpty}")
    @Size(max = 150, message = "{msg.tecnico.apellidos.size}")
    private String apellidos;

    @NotBlank(message = "{msg.tecnico.email.notEmpty}")
    @Email(message = "{msg.tecnico.email.valid}")
    @Size(max = 150, message = "{msg.tecnico.email.size}")
    private String email;

    @NotBlank(message = "{msg.tecnico.contrasena.notEmpty}")
    @Size(max = 255, message = "{msg.tecnico.contrasena.size}")
    private String contrasena;

    @NotBlank(message = "{msg.tecnico.telefono.notEmpty}")
    @Size(max = 20, message = "{msg.tecnico.telefono.size}")
    private String telefono;

    private boolean activo;

    @NotBlank(message = "{msg.tecnico.dni.notEmpty}")
    @Size(max = 20, message = "{msg.tecnico.dni.size}")
    private String dni;

    @NotBlank(message = "{msg.tecnico.fechaNacimiento.notEmpty}")
    private String fechaNacimiento;

    @NotBlank(message = "{msg.tecnico.direccion.notEmpty}")
    @Size(max = 255, message = "{msg.tecnico.direccion.size}")
    private String direccion;

    @NotBlank(message = "{msg.tecnico.localidad.notEmpty}")
    @Size(max = 100, message = "{msg.tecnico.localidad.size}")
    private String localidad;

    @NotBlank(message = "{msg.tecnico.provincia.notEmpty}")
    @Size(max = 100, message = "{msg.tecnico.provincia.size}")
    private String provincia;
}
