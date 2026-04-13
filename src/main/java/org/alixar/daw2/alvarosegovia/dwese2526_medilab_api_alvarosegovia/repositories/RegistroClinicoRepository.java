package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.RegistroClinico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroClinicoRepository extends JpaRepository<RegistroClinico, Long> {

    @EntityGraph(attributePaths = {"cita", "paciente", "paciente.profile", "tecnico", "tecnico.profile", "medico", "medico.profile"})
    List<RegistroClinico> findByCitaIdOrderByCreatedAtAsc(Long citaId);

    @EntityGraph(attributePaths = {"cita", "paciente", "paciente.profile", "tecnico", "tecnico.profile", "medico", "medico.profile"})
    List<RegistroClinico> findByEstadoOrderByCreatedAtAsc(RegistroClinico.EstadoRegistroClinico estado);

    @EntityGraph(attributePaths = {"cita", "paciente", "paciente.profile", "tecnico", "tecnico.profile", "medico", "medico.profile"})
    List<RegistroClinico> findByPacienteIdAndEstadoOrderByConfirmedAtDesc(Long pacienteId,
                                                                          RegistroClinico.EstadoRegistroClinico estado);
}
