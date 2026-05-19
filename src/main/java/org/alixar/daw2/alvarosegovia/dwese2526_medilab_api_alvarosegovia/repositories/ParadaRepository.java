package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad Parada.
 */
public interface ParadaRepository extends JpaRepository<Parada, Long> {
    @Override
    @EntityGraph(attributePaths = {"ruta", "ruta.trailer", "ruta.tecnicos", "ruta.tecnicos.profile"})
    List<Parada> findAll();

    @Override
    @EntityGraph(attributePaths = {"ruta", "ruta.trailer"})
    Page<Parada> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"ruta", "ruta.trailer", "ruta.tecnicos", "ruta.tecnicos.profile"})
    Optional<Parada> findById(Long id);

    @EntityGraph(attributePaths = {"ruta", "ruta.trailer", "ruta.tecnicos", "ruta.tecnicos.profile"})
    List<Parada> findByRutaIdOrderByFechaAscOrdenParadaAsc(Long rutaId);

    @EntityGraph(attributePaths = {"ruta", "ruta.trailer", "ruta.tecnicos", "ruta.tecnicos.profile"})
    List<Parada> findByActivaTrueAndFechaOrderByHoraInicioAsc(LocalDate fecha);

    @EntityGraph(attributePaths = {"ruta", "ruta.trailer", "ruta.tecnicos", "ruta.tecnicos.profile"})
    List<Parada> findByActivaTrueAndFechaGreaterThanEqualOrderByRutaIdAscFechaAscOrdenParadaAsc(LocalDate fecha);

    boolean existsByRutaIdAndFechaAndOrdenParada(Long rutaId, LocalDate fecha, Integer ordenParada);

    boolean existsByRutaIdAndFechaAndOrdenParadaAndIdNot(Long rutaId, LocalDate fecha, Integer ordenParada, Long id);

    boolean existsByRutaTrailerIdAndFecha(Long trailerId, LocalDate fecha);

    boolean existsByRutaTrailerIdAndFechaAndIdNot(Long trailerId, LocalDate fecha, Long id);
}
