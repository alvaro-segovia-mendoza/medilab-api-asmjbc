package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByTecnicoId(Long tecnicoId);

    List<Cita> findByDoctorId(Long doctorId);

    Page<Cita> findByPacienteId(Long pacienteId, Pageable pageable);

    Page<Cita> findByTecnicoId(Long tecnicoId, Pageable pageable);

    Page<Cita> findByDoctorId(Long doctorId, Pageable pageable);

    boolean existsByTecnicoIdAndEstadoInAndSlotFechaHoraInicioAfterAndSlotFechaHoraInicioBefore(Long tecnicoId,
                                                                                                 List<Cita.EstadoCita> estados,
                                                                                                 LocalDateTime lowerBoundExclusive,
                                                                                                 LocalDateTime upperBoundExclusive);

    boolean existsByTecnicoIdAndEstadoInAndSlotFechaHoraInicioAfterAndSlotFechaHoraInicioBeforeAndIdNot(Long tecnicoId,
                                                                                                          List<Cita.EstadoCita> estados,
                                                                                                          LocalDateTime lowerBoundExclusive,
                                                                                                          LocalDateTime upperBoundExclusive,
                                                                                                          Long citaId);

    boolean existsBySlotParadaIdAndEstadoIn(Long paradaId, List<Cita.EstadoCita> estados);

    boolean existsByTecnicoIdAndEstadoInAndSlotFechaHoraInicioGreaterThanEqualAndSlotParadaRutaId(Long tecnicoId,
                                                                                                   List<Cita.EstadoCita> estados,
                                                                                                   LocalDateTime fechaHoraInicio,
                                                                                                   Long rutaId);

    long countByTecnicoIdAndEstadoInAndSlotFechaHoraInicioGreaterThanEqualAndSlotParadaRutaId(Long tecnicoId,
                                                                                                List<Cita.EstadoCita> estados,
                                                                                                LocalDateTime fechaHoraInicio,
                                                                                                Long rutaId);

    @Query("SELECT c FROM Cita c WHERE (:estado IS NULL OR c.estado = :estado) AND (:pacienteId IS NULL OR c.paciente.id = :pacienteId)")
    Page<Cita> findByFilters(@Param("estado") Cita.EstadoCita estado,
                             @Param("pacienteId") Long pacienteId,
                             Pageable pageable);

}
