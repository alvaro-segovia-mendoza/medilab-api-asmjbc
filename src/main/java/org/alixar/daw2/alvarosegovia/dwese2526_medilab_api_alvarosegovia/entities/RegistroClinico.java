package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA que almacena el borrador tecnico y la revision medica de una cita.
 */
@Entity
@Table(name = "registros_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroClinico {

    public enum EstadoRegistroClinico {
        BORRADOR,
        PENDIENTE_REVISION,
        CONFIRMADO,
        RECHAZADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private User paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private User tecnico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private User medico;

    @Column(name = "tipo_prueba", nullable = false, length = 50)
    private String tipoPrueba;

    @Column(name = "resultado", nullable = false, columnDefinition = "TEXT")
    private String resultado;

    @Column(name = "observaciones_tecnico", columnDefinition = "TEXT")
    private String observacionesTecnico;

    @Column(name = "observaciones_medico", columnDefinition = "TEXT")
    private String observacionesMedico;

    @Column(name = "receta_o_solucion", columnDefinition = "TEXT")
    private String recetaOSolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoRegistroClinico estado = EstadoRegistroClinico.BORRADOR;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
