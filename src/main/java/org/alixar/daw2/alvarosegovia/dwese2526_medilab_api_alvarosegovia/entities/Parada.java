package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "paradas")
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

    @Column(name = "direccion", length = 150)
    private String direccion;

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
}
