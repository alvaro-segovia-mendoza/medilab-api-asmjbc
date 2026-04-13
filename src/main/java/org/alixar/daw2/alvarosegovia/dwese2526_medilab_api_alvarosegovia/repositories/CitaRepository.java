package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByTecnicoId(Long tecnicoId);

    List<Cita> findByDoctorId(Long doctorId);

    Page<Cita> findByPacienteId(Long pacienteId, Pageable pageable);

    Page<Cita> findByTecnicoId(Long tecnicoId, Pageable pageable);

    Page<Cita> findByDoctorId(Long doctorId, Pageable pageable);

    long countByParadaIdAndFechaHoraAndEstadoIn(Long paradaId,
                                                LocalDateTime fechaHora,
                                                List<Cita.EstadoCita> estados);

    boolean existsByTecnicoIdAndEstadoInAndFechaHoraAfterAndFechaHoraBefore(Long tecnicoId,
                                                                            List<Cita.EstadoCita> estados,
                                                                            LocalDateTime lowerBoundExclusive,
                                                                            LocalDateTime upperBoundExclusive);

}
