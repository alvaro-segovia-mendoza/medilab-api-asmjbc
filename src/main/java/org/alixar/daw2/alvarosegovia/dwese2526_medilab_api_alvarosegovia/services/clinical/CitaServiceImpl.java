package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.CitaMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.SlotCitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics.ParadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private static final long CITA_DURATION_MINUTES = 30;
    private static final LocalTime WORKDAY_START = LocalTime.of(9, 0);
    private static final LocalTime WORKDAY_END = LocalTime.of(15, 0);
    private static final List<Cita.EstadoCita> BLOCKING_APPOINTMENT_STATES = List.of(
            Cita.EstadoCita.PENDIENTE,
            Cita.EstadoCita.CONFIRMADA,
            Cita.EstadoCita.RESULTADOS_SUBIDOS,
            Cita.EstadoCita.RESULTADOS_APROBADOS
    );

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParadaService paradaService;

    @Autowired
    private SlotCitaRepository slotCitaRepository;

    /**
     * Obtiene una lista paginada de citas.
     */
    @Override
    public Page<CitaDTO> list(Pageable pageable) {
        return findVisibleCitas(pageable)
                .map(CitaMapper::toDTO);
    }

    /**
     * Obtiene una lista paginada de citas para el administrador con filtros opcionales.
     */
    @Override
    public Page<CitaDTO> listAdmin(Cita.EstadoCita estado, Long pacienteId, Pageable pageable) {
        return citaRepository.findByFilters(estado, pacienteId, pageable)
                .map(CitaMapper::toDTO);
    }

    /**
     * Obtiene los datos de una cita para su edición.
     */
    @Override
    public CitaUpdateDTO getForEdit(Long id) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("cita", "id", id)
                );
        assertCanAccessCita(cita);

        return CitaMapper.toUpdateDTO(cita);
    }

    /**
     * Crea una nueva cita.
     */
    @Override
    public CitaDTO create(CitaCreateDTO dto) {
        assertPatientOwnership(dto.getPacienteId());
        SlotCita slot = getReservableSlotWithLock(dto.getSlotId(), null);
        User tecnicoDisponible = findAvailableTechnician(slot, null);

        Cita cita = CitaMapper.toEntity(dto);
        cita.setPaciente(findRequiredUser(dto.getPacienteId(), "paciente"));
        cita.setSlot(slot);
        cita.setTecnico(tecnicoDisponible);
        cita.setDoctor(null);

        cita = citaRepository.save(cita);
        slot.setEstado(SlotCita.EstadoSlot.RESERVADO);
        slotCitaRepository.save(slot);

        return CitaMapper.toDTO(cita);
    }


    /**
     * Actualiza una cita existente.
     */
    @Override
    public CitaDTO update(CitaUpdateDTO dto) {
        Cita cita = citaRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("cita", "id", dto.getId())
                );
        assertCanModifyCita(cita, dto);

        SlotCita previousSlot = cita.getSlot();
        SlotCita nextSlot = getReservableSlotWithLock(dto.getSlotId(), cita.getId());
        boolean slotChanged = previousSlot == null || !previousSlot.getId().equals(nextSlot.getId());

        cita.setTipoPrueba(dto.getTipoPrueba());
        cita.setEstado(dto.getEstadoCita());
        cita.setPaciente(findRequiredUser(dto.getPacienteId(), "paciente"));
        cita.setSlot(nextSlot);
        cita.setTecnico(resolveTechnicianForUpdate(dto, cita, nextSlot, slotChanged));
        cita.setDoctor(findOptionalUser(dto.getDoctorId(), "doctor"));

        cita = citaRepository.save(cita);
        if (slotChanged && previousSlot != null) {
            previousSlot.setEstado(SlotCita.EstadoSlot.DISPONIBLE);
            slotCitaRepository.save(previousSlot);
        }
        nextSlot.setEstado(SlotCita.EstadoSlot.RESERVADO);
        slotCitaRepository.save(nextSlot);

        return CitaMapper.toDTO(cita);
    }

    @Override
    public CitaDTO confirm(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cita", "id", id));
        assertCanManageCitaState(cita);

        if (cita.getEstado() != Cita.EstadoCita.PENDIENTE) {
            throw ApiBusinessException.badRequest("CITA_ONLY_PENDING_CAN_BE_CONFIRMED", "api.error.cita.onlyPendingCanBeConfirmed");
        }

        cita.setEstado(Cita.EstadoCita.CONFIRMADA);
        return CitaMapper.toDTO(citaRepository.save(cita));
    }

    @Override
    public CitaDTO cancel(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cita", "id", id));
        assertCanManageCitaState(cita);

        if (cita.getEstado() != Cita.EstadoCita.PENDIENTE && cita.getEstado() != Cita.EstadoCita.CONFIRMADA) {
            throw ApiBusinessException.badRequest("CITA_ONLY_PENDING_OR_CONFIRMED_CAN_BE_CANCELLED", "api.error.cita.onlyPendingOrConfirmedCanBeCancelled");
        }

        cita.setEstado(Cita.EstadoCita.CANCELADA);
        if (cita.getSlot() != null) {
            cita.getSlot().setEstado(SlotCita.EstadoSlot.DISPONIBLE);
            slotCitaRepository.save(cita.getSlot());
        }
        return CitaMapper.toDTO(citaRepository.save(cita));
    }


    /**
     * Elimina una cita por su identificador.
     */
    @Override
    public void delete(Long id) {

        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("cita", "id", id);
        }

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("cita", "id", id));

        if (cita.getSlot() != null) {
            cita.getSlot().setEstado(SlotCita.EstadoSlot.DISPONIBLE);
            slotCitaRepository.save(cita.getSlot());
        }

        citaRepository.deleteById(id);
    }

    /**
     * Obtiene el detalle completo de una cita.
     */
    @Override
    public CitaDetailDTO getDetail(Long id) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("cita", "id", id)
                );
        assertCanAccessCita(cita);

        return CitaMapper.toDetailDTO(cita);
    }

    @Override
    public List<CitaDTO> getAllCitas() {
        List<Cita> regions = findVisibleCitas();

        // Convertimos cada entidad a DTO usando el mapper
        return regions.stream()
                .map(CitaMapper::toDTO)
                .toList();
    }

    private User findRequiredUser(Long userId, String fieldName) {
        if (userId == null) {
            throw new ResourceNotFoundException("user", "id", null);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(fieldName, "id", userId));
    }

    private User findOptionalUser(Long userId, String fieldName) {
        if (userId == null) {
            return null;
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(fieldName, "id", userId));
    }

    private User findAvailableTechnician(SlotCita slot, Long excludingCitaId) {
        LocalDateTime startAt = slot.getFechaHoraInicio();
        LocalDateTime lowerBoundExclusive = startAt.minusMinutes(CITA_DURATION_MINUTES);
        LocalDateTime upperBoundExclusive = startAt.plusMinutes(CITA_DURATION_MINUTES);
        var ruta = slot.getParada().getRuta();
        if (ruta == null || ruta.getTecnicos() == null || ruta.getTecnicos().isEmpty()) {
            throw ApiBusinessException.badRequest("CITA_NO_TECNICO_AVAILABLE", "api.error.cita.noTecnicoAvailable");
        }

        return ruta.getTecnicos().stream()
                .filter(tecnico -> isTechnicianAvailable(tecnico.getId(), lowerBoundExclusive, upperBoundExclusive, excludingCitaId))
                .min(Comparator
                        .comparingLong((User tecnico) -> countFutureActiveCitasForRoute(tecnico.getId(), ruta.getId(), startAt))
                        .thenComparing(User::getId))
                .orElseThrow(() -> ApiBusinessException.badRequest("CITA_NO_TECNICO_AVAILABLE", "api.error.cita.noTecnicoAvailable"));
    }

    private User requireAssignedAvailableTechnician(Long tecnicoId, SlotCita slot, Long excludingCitaId) {
        User tecnico = findOptionalUser(tecnicoId, "tecnico");
        if (!isTecnicoAssignedToRoute(tecnico, slot)) {
            throw ApiBusinessException.badRequest("CITA_TECNICO_NOT_ASSIGNED_TO_ROUTE", "api.error.cita.tecnicoNotAssignedToRoute");
        }

        LocalDateTime lowerBoundExclusive = slot.getFechaHoraInicio().minusMinutes(CITA_DURATION_MINUTES);
        LocalDateTime upperBoundExclusive = slot.getFechaHoraInicio().plusMinutes(CITA_DURATION_MINUTES);
        if (!isTechnicianAvailable(tecnico.getId(), lowerBoundExclusive, upperBoundExclusive, excludingCitaId)) {
            throw ApiBusinessException.badRequest("CITA_NO_TECNICO_AVAILABLE", "api.error.cita.noTecnicoAvailable");
        }
        return tecnico;
    }

    private long countFutureActiveCitasForRoute(Long tecnicoId, Long rutaId, LocalDateTime from) {
        return citaRepository.countByTecnicoIdAndEstadoInAndSlotFechaHoraInicioGreaterThanEqualAndSlotParadaRutaId(
                tecnicoId,
                BLOCKING_APPOINTMENT_STATES,
                from,
                rutaId
        );
    }

    private Page<Cita> findVisibleCitas(Pageable pageable) {
        if (hasRole("ROLE_ADMIN")) {
            return citaRepository.findAll(pageable);
        }
        if (hasRole("ROLE_PACIENTE")) {
            return citaRepository.findByPacienteId(getCurrentUser().getId(), pageable);
        }
        if (hasRole("ROLE_TECNICO")) {
            return citaRepository.findByTecnicoId(getCurrentUser().getId(), pageable);
        }
        if (hasRole("ROLE_MEDICO")) {
            return citaRepository.findByDoctorId(getCurrentUser().getId(), pageable);
        }
        throw ApiBusinessException.badRequest("CITA_ACCESS_DENIED_LIST", "api.error.cita.accessDeniedList");
    }

    private List<Cita> findVisibleCitas() {
        if (hasRole("ROLE_ADMIN")) {
            return citaRepository.findAll();
        }
        if (hasRole("ROLE_PACIENTE")) {
            return citaRepository.findByPacienteId(getCurrentUser().getId());
        }
        if (hasRole("ROLE_TECNICO")) {
            return citaRepository.findByTecnicoId(getCurrentUser().getId());
        }
        if (hasRole("ROLE_MEDICO")) {
            return citaRepository.findByDoctorId(getCurrentUser().getId());
        }
        throw ApiBusinessException.badRequest("CITA_ACCESS_DENIED_LIST", "api.error.cita.accessDeniedList");
    }

    private SlotCita getReservableSlotWithLock(Long slotId, Long excludingCitaId) {
        SlotCita slot = slotCitaRepository.findWithLockById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("slot", "id", slotId));
        validateAppointmentSlot(slot, excludingCitaId);
        return slot;
    }

    private void validateAppointmentSlot(SlotCita slot, Long excludingCitaId) {
        LocalDateTime fechaHora = slot.getFechaHoraInicio();
        LocalTime time = fechaHora.toLocalTime();

        if (slot.getEstado() != SlotCita.EstadoSlot.DISPONIBLE && excludingCitaId == null) {
            throw ApiBusinessException.badRequest("CITA_SLOT_UNAVAILABLE", "api.error.cita.slotUnavailable");
        }
        if (excludingCitaId != null && slot.getEstado() != SlotCita.EstadoSlot.DISPONIBLE) {
            Cita existing = citaRepository.findById(excludingCitaId)
                    .orElseThrow(() -> new ResourceNotFoundException("cita", "id", excludingCitaId));
            if (existing.getSlot() == null || !slot.getId().equals(existing.getSlot().getId())) {
                throw ApiBusinessException.badRequest("CITA_NEW_SLOT_UNAVAILABLE", "api.error.cita.newSlotUnavailable");
            }
        }
        if (slot.getParada() == null) {
            throw ApiBusinessException.badRequest("CITA_SLOT_WITHOUT_PARADA", "api.error.cita.slotWithoutParada");
        }
        var parada = slot.getParada();
        if (!parada.isActiva()) {
            throw ApiBusinessException.badRequest("CITA_PARADA_INACTIVE", "api.error.cita.paradaInactive");
        }
        if (parada.getRuta() == null || !parada.getRuta().isActiva()) {
            throw ApiBusinessException.badRequest("CITA_RUTA_INACTIVE", "api.error.cita.rutaInactive");
        }
        if (parada.getRuta().getTrailer() == null || !parada.getRuta().getTrailer().isActivo()) {
            throw ApiBusinessException.badRequest("CITA_TRAILER_INACTIVE", "api.error.cita.trailerInactive");
        }
        if (!fechaHora.toLocalDate().equals(parada.getFecha()) || !slot.getFechaHoraFin().toLocalDate().equals(parada.getFecha())) {
            throw ApiBusinessException.badRequest("CITA_SLOT_DIFFERENT_DATE", "api.error.cita.slotDifferentDate");
        }
        if (time.isBefore(WORKDAY_START) || slot.getFechaHoraFin().toLocalTime().isAfter(WORKDAY_END)) {
            throw ApiBusinessException.badRequest("CITA_OUTSIDE_DAILY_SCHEDULE", "api.error.cita.outsideDailySchedule");
        }
        if (time.isBefore(parada.getHoraInicio()) || slot.getFechaHoraFin().toLocalTime().isAfter(parada.getHoraFin())) {
            throw ApiBusinessException.badRequest("CITA_OUTSIDE_PARADA_SCHEDULE", "api.error.cita.outsideParadaSchedule");
        }
        findAvailableTechnician(slot, excludingCitaId);
    }

    private boolean isTechnicianAvailable(Long tecnicoId,
                                          LocalDateTime lowerBoundExclusive,
                                          LocalDateTime upperBoundExclusive,
                                          Long excludingCitaId) {
        return excludingCitaId == null
                ? !citaRepository.existsByTecnicoIdAndEstadoInAndSlotFechaHoraInicioAfterAndSlotFechaHoraInicioBefore(
                tecnicoId,
                BLOCKING_APPOINTMENT_STATES,
                lowerBoundExclusive,
                upperBoundExclusive
        )
                : !citaRepository.existsByTecnicoIdAndEstadoInAndSlotFechaHoraInicioAfterAndSlotFechaHoraInicioBeforeAndIdNot(
                tecnicoId,
                BLOCKING_APPOINTMENT_STATES,
                lowerBoundExclusive,
                upperBoundExclusive,
                excludingCitaId
        );
    }

    private User resolveTechnicianForUpdate(CitaUpdateDTO dto, Cita cita, SlotCita nextSlot, boolean slotChanged) {
        if (dto.getTecnicoId() != null) {
            return requireAssignedAvailableTechnician(dto.getTecnicoId(), nextSlot, cita.getId());
        }
        if (!slotChanged && cita.getTecnico() != null && isTecnicoAssignedToRoute(cita.getTecnico(), nextSlot)) {
            return cita.getTecnico();
        }
        return findAvailableTechnician(nextSlot, cita.getId());
    }

    private boolean isTecnicoAssignedToRoute(User tecnico, SlotCita slot) {
        return tecnico != null
                && slot.getParada() != null
                && slot.getParada().getRuta() != null
                && slot.getParada().getRuta().getTecnicos() != null
                && slot.getParada().getRuta().getTecnicos().stream()
                .anyMatch(routeTecnico -> routeTecnico.getId().equals(tecnico.getId()));
    }

    private void assertPatientOwnership(Long pacienteId) {
        if (hasRole("ROLE_PACIENTE") && !getCurrentUser().getId().equals(pacienteId)) {
            throw ApiBusinessException.badRequest("CITA_ONLY_OWN_USER_BOOKING", "api.error.cita.onlyOwnUserBooking");
        }
    }

    private void assertCanAccessCita(Cita cita) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }
        if (hasRole("ROLE_PACIENTE") && cita.getPaciente() != null && getCurrentUser().getId().equals(cita.getPaciente().getId())) {
            return;
        }
        if (hasRole("ROLE_TECNICO") && cita.getTecnico() != null && getCurrentUser().getId().equals(cita.getTecnico().getId())) {
            return;
        }
        if (hasRole("ROLE_MEDICO") && cita.getDoctor() != null && getCurrentUser().getId().equals(cita.getDoctor().getId())) {
            return;
        }
        throw ApiBusinessException.badRequest("CITA_ACCESS_DENIED", "api.error.cita.accessDenied");
    }

    private void assertCanModifyCita(Cita cita, CitaUpdateDTO dto) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }
        if (hasRole("ROLE_TECNICO")) {
            if (cita.getTecnico() == null || !getCurrentUser().getId().equals(cita.getTecnico().getId())) {
                throw ApiBusinessException.badRequest("CITA_ONLY_ASSIGNED_TECNICO_CAN_MODIFY", "api.error.cita.onlyAssignedTecnicoCanModify");
            }
            if (dto.getTecnicoId() != null && !getCurrentUser().getId().equals(dto.getTecnicoId())) {
                throw ApiBusinessException.badRequest("CITA_CANNOT_REASSIGN_TECNICO", "api.error.cita.cannotReassignTecnico");
            }
            if (dto.getPacienteId() != null && cita.getPaciente() != null && !cita.getPaciente().getId().equals(dto.getPacienteId())) {
                throw ApiBusinessException.badRequest("CITA_CANNOT_CHANGE_PACIENTE", "api.error.cita.cannotChangePaciente");
            }
            return;
        }
        throw ApiBusinessException.badRequest("CITA_MODIFY_DENIED", "api.error.cita.modifyDenied");
    }

    private void assertCanManageCitaState(Cita cita) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }
        if (hasRole("ROLE_TECNICO") && cita.getTecnico() != null && getCurrentUser().getId().equals(cita.getTecnico().getId())) {
            return;
        }
        if (hasRole("ROLE_PACIENTE") && cita.getPaciente() != null && getCurrentUser().getId().equals(cita.getPaciente().getId())) {
            return;
        }
        throw ApiBusinessException.badRequest("CITA_STATUS_DENIED", "api.error.cita.statusDenied");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("api.error.authenticatedUserMissing");
        }

        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", authentication.getName()));
    }

    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> roleName.equals(a.getAuthority()));
    }
}
