package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA que modela una ruta operativa asociada a un trailer y a sus tecnicos.
 */
@Entity
@Table(name = "rutas")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "tecnicos")
@ToString(exclude = "tecnicos")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "origen", nullable = false, length = 100)
    private String origen;

    @Column(name = "destino", nullable = false, length = 100)
    private String destino;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trailer_id", nullable = false)
    private Trailer trailer;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ruta_tecnicos",
            joinColumns = @JoinColumn(name = "ruta_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tecnico_id", referencedColumnName = "id")
    )
    private Set<User> tecnicos = new HashSet<>();
}
