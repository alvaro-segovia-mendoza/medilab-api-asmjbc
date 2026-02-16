package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase `Tecnico` representa a un técnico del sistema.
 * Contiene información personal y de contacto, incluyendo:
 * - id_usuario: identificador único del técnico.
 * - nombre, apellidos: datos personales.
 * - email: correo electrónico.
 * - contraseña: clave de acceso.
 * - telefono: número de contacto.
 * - activo: indica si el técnico está habilitado.
 * - dni: documento nacional de identidad.
 * - fecha_nacimiento: fecha de nacimiento del técnico.
 * - direccion, localidad, provincia: información de dirección.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tecnico")
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "dni", nullable = false, unique = true, length = 20)
    private String dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    private String fechaNacimiento;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String localidad;

    @Column(nullable = false, length = 100)
    private String provincia;

    /**
     * Lista de citas pertenecientes al técnico.
     * - LAZY: no se cargan hasta que se accede a 'citas'.
     * - mappedBy: el dueño de la relación es Tecnico.citas.
     * - Con cascade ALL: Así se borrarían las citas asociadas al técnico si se elimina el técnico.
     */
    @OneToMany(
            mappedBy = "tecnico",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = false
    )
    @ToString.Exclude           // Evita bucles en toString()
    @EqualsAndHashCode.Exclude  // Evita ciclos en equals/hashCode
    private List<Cita> citas = new ArrayList<>();
}
