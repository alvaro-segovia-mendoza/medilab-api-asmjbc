package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import jakarta.persistence.LockModeType;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotCitaRepository extends JpaRepository<SlotCita, Long> {

    @Override
    @EntityGraph(attributePaths = {"parada", "parada.ruta", "parada.ruta.trailer"})
    Optional<SlotCita> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"parada", "parada.ruta", "parada.ruta.trailer"})
    Optional<SlotCita> findWithLockById(Long id);

    @EntityGraph(attributePaths = {"parada", "parada.ruta", "parada.ruta.trailer"})
    List<SlotCita> findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(Long paradaId);

    List<SlotCita> findByParadaIdAndFechaHoraInicioOrderByCupoNumeroAsc(Long paradaId, LocalDateTime fechaHoraInicio);

    void deleteByParadaId(Long paradaId);
}
