package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RegistroClinicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RegistroClinicoReviewDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.RegistroClinico;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.RegistroClinicoMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RegistroClinicoRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistroClinicoServiceImpl implements RegistroClinicoService {

    @Autowired
    private RegistroClinicoRepository registroClinicoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RegistroClinicoDTO create(RegistroClinicoCreateDTO dto) {
        Cita cita = findCita(dto.getCitaId());
        User tecnico = resolveCurrentTechnician(dto.getTecnicoId());

        validateTechnicalDraftCreation(cita, tecnico);

        RegistroClinico registro = RegistroClinico.builder()
                .cita(cita)
                .paciente(cita.getPaciente())
                .tecnico(tecnico)
                .medico(cita.getDoctor())
                .tipoPrueba(cita.getTipoPrueba())
                .resultado(dto.getResultado())
                .observacionesTecnico(dto.getObservacionesTecnico())
                .estado(RegistroClinico.EstadoRegistroClinico.BORRADOR)
                .build();

        return RegistroClinicoMapper.toDTO(registroClinicoRepository.save(registro));
    }

    @Override
    public RegistroClinicoDTO submitForReview(Long id) {
        RegistroClinico registro = findRegistro(id);
        assertCanAccessRegistro(registro);

        if (registro.getEstado() != RegistroClinico.EstadoRegistroClinico.BORRADOR) {
            throw new IllegalArgumentException("Solo los registros en borrador pueden enviarse a revisión.");
        }

        registro.setEstado(RegistroClinico.EstadoRegistroClinico.PENDIENTE_REVISION);
        registro.getCita().setEstado(Cita.EstadoCita.RESULTADOS_SUBIDOS);

        return RegistroClinicoMapper.toDTO(registroClinicoRepository.save(registro));
    }

    @Override
    public RegistroClinicoDTO review(Long id, RegistroClinicoReviewDTO dto) {
        RegistroClinico registro = findRegistro(id);
        assertCanReviewRegistro(registro);
        User medico = resolveReviewingDoctor(null);

        if (registro.getEstado() != RegistroClinico.EstadoRegistroClinico.PENDIENTE_REVISION) {
            throw new IllegalArgumentException("Solo los registros pendientes de revisión pueden ser evaluados.");
        }

        if (registro.getCita().getDoctor() != null && !registro.getCita().getDoctor().getId().equals(medico.getId())) {
            throw new IllegalArgumentException("La cita ya tiene un médico asignado distinto.");
        }

        registro.setMedico(medico);
        registro.getCita().setDoctor(medico);
        registro.setObservacionesMedico(dto.getObservacionesMedico());
        registro.setRecetaOSolucion(dto.getRecetaOSolucion());

        if (Boolean.TRUE.equals(dto.getAprobado())) {
            registro.setEstado(RegistroClinico.EstadoRegistroClinico.CONFIRMADO);
            registro.setConfirmedAt(LocalDateTime.now());
            registro.getCita().setEstado(Cita.EstadoCita.RESULTADOS_APROBADOS);
        } else {
            registro.setEstado(RegistroClinico.EstadoRegistroClinico.RECHAZADO);
            registro.setConfirmedAt(null);
            registro.getCita().setEstado(Cita.EstadoCita.CONFIRMADA);
        }

        return RegistroClinicoMapper.toDTO(registroClinicoRepository.save(registro));
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroClinicoDTO getDetail(Long id) {
        RegistroClinico registro = findRegistro(id);
        assertCanAccessRegistro(registro);
        return RegistroClinicoMapper.toDTO(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroClinicoDTO> listByCita(Long citaId) {
        List<RegistroClinico> registros = registroClinicoRepository.findByCitaIdOrderByCreatedAtAsc(citaId).stream()
                .filter(this::canAccessRegistro)
                .toList();
        return RegistroClinicoMapper.toDTOList(registros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroClinicoDTO> listPendingReview() {
        if (!hasRole("ROLE_MEDICO") && !hasRole("ROLE_ADMIN")) {
            throw new IllegalArgumentException("No tienes permisos para consultar registros pendientes.");
        }
        return RegistroClinicoMapper.toDTOList(
                registroClinicoRepository.findByEstadoOrderByCreatedAtAsc(
                        RegistroClinico.EstadoRegistroClinico.PENDIENTE_REVISION
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroClinicoDTO> getPatientHistory(Long pacienteId) {
        if (hasRole("ROLE_PACIENTE") && !getCurrentUser().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("Solo puedes consultar tu propio historial clínico.");
        }
        return RegistroClinicoMapper.toDTOList(
                registroClinicoRepository.findByPacienteIdAndEstadoOrderByConfirmedAtDesc(
                        pacienteId,
                        RegistroClinico.EstadoRegistroClinico.CONFIRMADO
                )
        );
    }

    private RegistroClinico findRegistro(Long id) {
        return registroClinicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("registroClinico", "id", id));
    }

    private Cita findCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cita", "id", id));
    }

    private User findUser(Long id, String resource) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(resource, "id", id));
    }

    private void validateTechnicalDraftCreation(Cita cita, User tecnico) {
        if (cita.getTecnico() == null) {
            throw new IllegalArgumentException("La cita no tiene técnico asignado.");
        }

        if (!cita.getTecnico().getId().equals(tecnico.getId())) {
            throw new IllegalArgumentException("Solo el técnico asignado puede crear registros clínicos para esta cita.");
        }

        if (cita.getEstado() != Cita.EstadoCita.CONFIRMADA && cita.getEstado() != Cita.EstadoCita.RESULTADOS_SUBIDOS) {
            throw new IllegalArgumentException("La cita debe estar confirmada para crear un registro clínico.");
        }
    }

    private User resolveCurrentTechnician(Long tecnicoId) {
        if (hasRole("ROLE_ADMIN")) {
            return findUser(tecnicoId, "tecnico");
        }

        if (!hasRole("ROLE_TECNICO")) {
            throw new IllegalArgumentException("No tienes permisos para crear registros clínicos.");
        }

        User currentUser = getCurrentUser();
        if (tecnicoId != null && !currentUser.getId().equals(tecnicoId)) {
            throw new IllegalArgumentException("Solo puedes crear registros clínicos como técnico autenticado.");
        }
        return currentUser;
    }

    private User resolveReviewingDoctor(Long medicoId) {
        if (hasRole("ROLE_ADMIN")) {
            return findUser(medicoId, "medico");
        }

        if (!hasRole("ROLE_MEDICO")) {
            throw new IllegalArgumentException("No tienes permisos para revisar registros clínicos.");
        }

        User currentUser = getCurrentUser();
        if (medicoId != null && !currentUser.getId().equals(medicoId)) {
            throw new IllegalArgumentException("Solo puedes revisar registros clínicos como médico autenticado.");
        }
        return currentUser;
    }

    private void assertCanReviewRegistro(RegistroClinico registro) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }
        if (!hasRole("ROLE_MEDICO")) {
            throw new IllegalArgumentException("No tienes permisos para revisar este registro clínico.");
        }
        if (registro.getEstado() == RegistroClinico.EstadoRegistroClinico.PENDIENTE_REVISION) {
            return;
        }
        if (registro.getMedico() != null && getCurrentUser().getId().equals(registro.getMedico().getId())) {
            return;
        }
        throw new IllegalArgumentException("No tienes permisos para revisar este registro clínico.");
    }

    private void assertCanAccessRegistro(RegistroClinico registro) {
        if (!canAccessRegistro(registro)) {
            throw new IllegalArgumentException("No tienes permisos para acceder a este registro clínico.");
        }
    }

    private boolean canAccessRegistro(RegistroClinico registro) {
        if (hasRole("ROLE_ADMIN")) {
            return true;
        }
        if (hasRole("ROLE_PACIENTE") && registro.getPaciente() != null) {
            return getCurrentUser().getId().equals(registro.getPaciente().getId());
        }
        if (hasRole("ROLE_TECNICO") && registro.getTecnico() != null) {
            return getCurrentUser().getId().equals(registro.getTecnico().getId());
        }
        if (hasRole("ROLE_MEDICO")) {
            return registro.getEstado() == RegistroClinico.EstadoRegistroClinico.PENDIENTE_REVISION
                    || (registro.getMedico() != null && getCurrentUser().getId().equals(registro.getMedico().getId()));
        }
        return false;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No hay usuario autenticado en el contexto de seguridad.");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", authentication.getName()));
    }

    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> roleName.equals(a.getAuthority()));
    }
}
