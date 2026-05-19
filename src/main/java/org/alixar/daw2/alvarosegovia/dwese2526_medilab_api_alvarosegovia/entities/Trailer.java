package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que representa un trailer movil disponible para operar rutas.
 */
@Entity
@Table(name = "trailers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trailer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "descripcion", length = 255)
    private String descripcion;
}
