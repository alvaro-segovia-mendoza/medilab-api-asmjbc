package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "slot_cita",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_slot_parada_inicio_cupo", columnNames = {"parada_id", "fecha_hora_inicio", "cupo_numero"})
        },
        indexes = {
                @Index(name = "idx_slot_estado_inicio", columnList = "estado, fecha_hora_inicio")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotCita {

    public enum EstadoSlot {
        DISPONIBLE,
        RESERVADO,
        NO_DISPONIBLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parada_id", nullable = false)
    private Parada parada;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(name = "cupo_numero", nullable = false)
    private Integer cupoNumero;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSlot estado = EstadoSlot.DISPONIBLE;
}
