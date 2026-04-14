package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ParadaRepository extends JpaRepository<Parada, Long> {
    @Override
    @EntityGraph(attributePaths = {"ruta", "ruta.trailer"})
    List<Parada> findAll();

    @Override
    @EntityGraph(attributePaths = {"ruta", "ruta.trailer"})
    Page<Parada> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"ruta", "ruta.trailer"})
    Optional<Parada> findById(Long id);

    @EntityGraph(attributePaths = {"ruta", "ruta.trailer"})
    List<Parada> findByRutaIdOrderByFechaAscOrdenParadaAsc(Long rutaId);

    @EntityGraph(attributePaths = {"ruta", "ruta.trailer"})
    List<Parada> findByActivaTrueAndFechaOrderByHoraInicioAsc(LocalDate fecha);

    @EntityGraph(attributePaths = {"ruta", "ruta.trailer"})
    List<Parada> findByActivaTrueAndFechaGreaterThanEqualOrderByRutaIdAscFechaAscOrdenParadaAsc(LocalDate fecha);

    boolean existsByRutaIdAndFechaAndOrdenParada(Long rutaId, LocalDate fecha, Integer ordenParada);

    boolean existsByRutaIdAndFechaAndOrdenParadaAndIdNot(Long rutaId, LocalDate fecha, Integer ordenParada, Long id);

    boolean existsByRutaTrailerIdAndFecha(Long trailerId, LocalDate fecha);

    boolean existsByRutaTrailerIdAndFechaAndIdNot(Long trailerId, LocalDate fecha, Long id);

    @Query("""
            select count(p) > 0
            from Parada p
            where p.ruta.id = :rutaId
              and p.fecha = :fecha
              and p.horaInicio < :horaFin
              and p.horaFin > :horaInicio
            """)
    boolean existsSolapamientoEnRutaYFecha(@Param("rutaId") Long rutaId,
                                           @Param("fecha") LocalDate fecha,
                                           @Param("horaInicio") LocalTime horaInicio,
                                           @Param("horaFin") LocalTime horaFin);

    @Query("""
            select count(p) > 0
            from Parada p
            where p.ruta.id = :rutaId
              and p.fecha = :fecha
              and p.id <> :id
              and p.horaInicio < :horaFin
              and p.horaFin > :horaInicio
            """)
    boolean existsSolapamientoEnRutaYFechaExcluyendoId(@Param("rutaId") Long rutaId,
                                                       @Param("fecha") LocalDate fecha,
                                                       @Param("horaInicio") LocalTime horaInicio,
                                                       @Param("horaFin") LocalTime horaFin,
                                                       @Param("id") Long id);
}
