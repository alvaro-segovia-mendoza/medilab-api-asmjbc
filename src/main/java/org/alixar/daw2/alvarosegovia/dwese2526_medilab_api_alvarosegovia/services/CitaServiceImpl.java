package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.CitaMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.SlotCitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private static final String ROLE_TECNICO = "ROLE_TECNICO";
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
        User tecnicoDisponible = findAvailableTechnician(slot.getFechaHoraInicio(), null);

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
            throw new IllegalArgumentException("Solo las citas pendientes pueden confirmarse.");
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
            throw new IllegalArgumentException("Solo las citas pendientes o confirmadas pueden cancelarse.");
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

    private User findAvailableTechnician(LocalDateTime startAt, Long excludingCitaId) {
        LocalDateTime lowerBoundExclusive = startAt.minusMinutes(CITA_DURATION_MINUTES);
        LocalDateTime upperBoundExclusive = startAt.plusMinutes(CITA_DURATION_MINUTES);

        return userRepository.findDistinctByRolesNameOrderByIdAsc(ROLE_TECNICO).stream()
                .filter(tecnico -> isTechnicianAvailable(tecnico.getId(), lowerBoundExclusive, upperBoundExclusive, excludingCitaId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("tecnicoDisponible", "fechaHora", startAt));
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
        throw new IllegalArgumentException("No tienes permisos para consultar citas.");
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
        throw new IllegalArgumentException("No tienes permisos para consultar citas.");
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
            throw new IllegalArgumentException("El slot seleccionado ya no esta disponible.");
        }
        if (excludingCitaId != null && slot.getEstado() != SlotCita.EstadoSlot.DISPONIBLE) {
            Cita existing = citaRepository.findById(excludingCitaId)
                    .orElseThrow(() -> new ResourceNotFoundException("cita", "id", excludingCitaId));
            if (existing.getSlot() == null || !slot.getId().equals(existing.getSlot().getId())) {
                throw new IllegalArgumentException("El nuevo slot seleccionado ya no esta disponible.");
            }
        }
        if (slot.getParada() == null) {
            throw new IllegalArgumentException("El slot no esta asociado a ninguna parada.");
        }
        var parada = slot.getParada();
        if (!parada.isActiva()) {
            throw new IllegalArgumentException("La parada no está activa para nuevas reservas.");
        }
        if (parada.getRuta() == null || !parada.getRuta().isActiva()) {
            throw new IllegalArgumentException("La ruta de la parada no está activa.");
        }
        if (parada.getRuta().getTrailer() == null || !parada.getRuta().getTrailer().isActivo()) {
            throw new IllegalArgumentException("El trailer asignado a la ruta no está activo.");
        }
        if (!fechaHora.toLocalDate().equals(parada.getFecha()) || !slot.getFechaHoraFin().toLocalDate().equals(parada.getFecha())) {
            throw new IllegalArgumentException("El slot debe pertenecer a la misma fecha operativa de la parada.");
        }
        if (time.isBefore(WORKDAY_START) || slot.getFechaHoraFin().toLocalTime().isAfter(WORKDAY_END)) {
            throw new IllegalArgumentException("La cita debe estar dentro del horario de 09:00 a 15:00.");
        }
        if (time.isBefore(parada.getHoraInicio()) || slot.getFechaHoraFin().toLocalTime().isAfter(parada.getHoraFin())) {
            throw new IllegalArgumentException("La cita debe estar dentro de la franja horaria de la parada.");
        }
        if (findAvailableTechnician(slot.getFechaHoraInicio(), excludingCitaId) == null) {
            throw new IllegalArgumentException("No hay tecnicos disponibles para el slot seleccionado.");
        }
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
            return findOptionalUser(dto.getTecnicoId(), "tecnico");
        }
        if (!slotChanged && cita.getTecnico() != null) {
            return cita.getTecnico();
        }
        return findAvailableTechnician(nextSlot.getFechaHoraInicio(), cita.getId());
    }

    private void assertPatientOwnership(Long pacienteId) {
        if (hasRole("ROLE_PACIENTE") && !getCurrentUser().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("Solo puedes reservar citas para tu propio usuario.");
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
        throw new IllegalArgumentException("No tienes permisos para acceder a esta cita.");
    }

    private void assertCanModifyCita(Cita cita, CitaUpdateDTO dto) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }
        if (hasRole("ROLE_TECNICO")) {
            if (cita.getTecnico() == null || !getCurrentUser().getId().equals(cita.getTecnico().getId())) {
                throw new IllegalArgumentException("Solo el técnico asignado puede modificar esta cita.");
            }
            if (dto.getTecnicoId() != null && !getCurrentUser().getId().equals(dto.getTecnicoId())) {
                throw new IllegalArgumentException("No puedes reasignar la cita a otro técnico.");
            }
            if (dto.getPacienteId() != null && cita.getPaciente() != null && !cita.getPaciente().getId().equals(dto.getPacienteId())) {
                throw new IllegalArgumentException("No puedes cambiar el paciente de la cita.");
            }
            return;
        }
        throw new IllegalArgumentException("No tienes permisos para modificar esta cita.");
    }

    private void assertCanManageCitaState(Cita cita) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }
        if (hasRole("ROLE_TECNICO") && cita.getTecnico() != null && getCurrentUser().getId().equals(cita.getTecnico().getId())) {
            return;
        }
        throw new IllegalArgumentException("Solo el técnico asignado puede gestionar el estado de esta cita.");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No hay usuario autenticado en el contexto de seguridad.");
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
