package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * La clase `Cita` representa una cita en el laboratorio médico.
 * Contiene campos como `codigo`, `fechaHora`, `tipoPrueba`, `estado` y el `tecnico` asociado.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cita")
public class Cita {

    // Identificador único de la cita (AUTO_INCREMENT en la tabla 'cita').
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long id;

    // Código único de la cita (VARCHAR(50) NOT NULL).
    @Column(nullable = false, length = 50)
    private String codigo;

    // Fecha y hora de la cita (DATETIME NOT NULL).
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // Tipo de prueba asociada a la cita (VARCHAR(50) NOT NULL).
    @Column(name = "tipo_prueba", nullable = false, length = 50)
    private String tipoPrueba;

    // Estado de la cita representado como ENUM.
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    // Relación con el técnico responsable de la cita.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Tecnico tecnico;

    /**
     * Enum que define los posibles estados de una cita.
     */
    public enum EstadoCita {
        PENDIENTE, ACEPTADA, CANCELADA, FINALIZADA
    }

    /**
     * Constructor de conveniencia sin ID.
     * Útil para crear nuevas citas antes de que el ID se genere en la base de datos.
     *
     * @param codigo
     * @param fechaHora
     * @param tipoPrueba
     * @param estado
     * @param tecnico
     */
    public Cita(String codigo, LocalDateTime fechaHora, String tipoPrueba, EstadoCita estado, Tecnico tecnico) {
        this.codigo = codigo;
        this.fechaHora = fechaHora;
        this.tipoPrueba = tipoPrueba;
        this.estado = estado;
        this.tecnico = tecnico;
    }
}
