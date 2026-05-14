package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "paradas",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_parada_ruta_fecha_orden", columnNames = {"ruta_id", "fecha", "orden_parada"})
        },
        indexes = {
                @Index(name = "idx_parada_activa_fecha_hora", columnList = "activa, fecha, hora_inicio"),
                @Index(name = "idx_parada_activa_fecha_ruta_orden", columnList = "activa, fecha, ruta_id, orden_parada")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "municipio", nullable = false, length = 100)
    private String municipio;

    @Column(name = "provincia", nullable = false, length = 100)
    private String provincia;

    @Column(name = "direccion", length = 150)
    private String direccion;

    @Column(name = "latitud", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitud;

    @Column(name = "longitud", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitud;

    @Column(name = "orden_parada", nullable = false)
    private Integer ordenParada;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima = 1;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
