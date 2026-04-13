package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.SlotDisponibilidadDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.ParadaMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.ParadaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ParadaServiceImpl implements ParadaService {

    private static final String ROLE_TECNICO = "ROLE_TECNICO";
    private static final long CITA_DURATION_MINUTES = 30;
    private static final List<Cita.EstadoCita> BLOCKING_APPOINTMENT_STATES = List.of(
            Cita.EstadoCita.PENDIENTE,
            Cita.EstadoCita.CONFIRMADA,
            Cita.EstadoCita.RESULTADOS_SUBIDOS,
            Cita.EstadoCita.RESULTADOS_APROBADOS
    );

    @Autowired
    private ParadaRepository paradaRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<ParadaDTO> list() {
        return ParadaMapper.toDTOList(paradaRepository.findAll());
    }

    @Override
    public ParadaDTO getDetail(Long id) {
        return ParadaMapper.toDTO(getEntity(id));
    }

    @Override
    public ParadaDTO create(ParadaCreateDTO dto) {
        Ruta ruta = findRuta(dto.getRutaId());
        validateSchedule(dto.getHoraInicio(), dto.getHoraFin());
        validateOperationalConsistency(ruta, dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin());
        validateUniqueOrder(ruta.getId(), dto.getFecha(), dto.getOrdenParada(), null);
        validateOverlappingStops(ruta.getId(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin(), null);

        Parada parada = Parada.builder()
                .ruta(ruta)
                .nombre(dto.getNombre())
                .municipio(dto.getMunicipio())
                .direccion(dto.getDireccion())
                .ordenParada(dto.getOrdenParada())
                .fecha(dto.getFecha())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .activa(dto.getActiva() == null || dto.getActiva())
                .build();
        return ParadaMapper.toDTO(paradaRepository.save(parada));
    }

    @Override
    public ParadaDTO update(ParadaUpdateDTO dto) {
        Parada parada = getEntity(dto.getId());
        Ruta ruta = findRuta(dto.getRutaId());

        validateSchedule(dto.getHoraInicio(), dto.getHoraFin());
        validateOperationalConsistency(ruta, dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin());
        validateUniqueOrder(ruta.getId(), dto.getFecha(), dto.getOrdenParada(), dto.getId());
        validateOverlappingStops(ruta.getId(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin(), dto.getId());

        parada.setRuta(ruta);
        ParadaMapper.copyToExistingEntity(dto, parada);
        return ParadaMapper.toDTO(paradaRepository.save(parada));
    }

    @Override
    public void delete(Long id) {
        if (!paradaRepository.existsById(id)) {
            throw new ResourceNotFoundException("parada", "id", id);
        }
        paradaRepository.deleteById(id);
    }

    @Override
    public List<ParadaDTO> listByRuta(Long rutaId) {
        return ParadaMapper.toDTOList(paradaRepository.findByRutaIdOrderByFechaAscOrdenParadaAsc(rutaId));
    }

    @Override
    public List<ParadaDTO> listActiveByFecha(LocalDate fecha) {
        return ParadaMapper.toDTOList(paradaRepository.findByActivaTrueAndFechaOrderByHoraInicioAsc(fecha));
    }

    @Override
    public List<ParadaDTO> listActiveFromDate(LocalDate fecha) {
        return ParadaMapper.toDTOList(paradaRepository.findByActivaTrueAndFechaGreaterThanEqualOrderByRutaIdAscFechaAscOrdenParadaAsc(fecha));
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadParadaDTO getDisponibilidad(Long paradaId) {
        Parada parada = getEntity(paradaId);
        List<SlotDisponibilidadDTO> slots = new ArrayList<>();
        List<LocalDateTime> slotsDisponibles = new ArrayList<>();
        LocalDateTime cursor = LocalDateTime.of(parada.getFecha(), parada.getHoraInicio());
        LocalDateTime end = LocalDateTime.of(parada.getFecha(), parada.getHoraFin());

        while (!cursor.plusMinutes(CITA_DURATION_MINUTES).isAfter(end)) {
            int plazasDisponibles = calculateRemainingCapacity(parada, cursor);
            boolean reservable = plazasDisponibles > 0;

            slots.add(SlotDisponibilidadDTO.builder()
                    .fechaHora(cursor)
                    .reservasActivas((int) countActiveAppointments(parada.getId(), cursor))
                    .plazasDisponibles(plazasDisponibles)
                    .reservable(reservable)
                    .build());

            if (reservable) {
                slotsDisponibles.add(cursor);
            }
            cursor = cursor.plusMinutes(CITA_DURATION_MINUTES);
        }

        return DisponibilidadParadaDTO.builder()
                .paradaId(parada.getId())
                .paradaNombre(parada.getNombre())
                .municipio(parada.getMunicipio())
                .capacidadMaxima(parada.getCapacidadMaxima())
                .slots(slots)
                .slotsDisponibles(slotsDisponibles)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Parada getEntity(Long id) {
        return paradaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("parada", "id", id));
    }

    private Ruta findRuta(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ruta", "id", id));
    }

    private boolean hasAvailableTechnician(LocalDateTime startAt) {
        return countAvailableTechnicians(startAt) > 0;
    }

    private long countAvailableTechnicians(LocalDateTime startAt) {
        LocalDateTime lowerBoundExclusive = startAt.minusMinutes(CITA_DURATION_MINUTES);
        LocalDateTime upperBoundExclusive = startAt.plusMinutes(CITA_DURATION_MINUTES);

        return userRepository.findDistinctByRolesNameOrderByIdAsc(ROLE_TECNICO).stream()
                .map(User::getId)
                .filter(tecnicoId -> !citaRepository.existsByTecnicoIdAndEstadoInAndFechaHoraAfterAndFechaHoraBefore(
                        tecnicoId,
                        BLOCKING_APPOINTMENT_STATES,
                        lowerBoundExclusive,
                        upperBoundExclusive
                ))
                .count();
    }

    private long countActiveAppointments(Long paradaId, LocalDateTime slotStartAt) {
        return citaRepository.countByParadaIdAndFechaHoraAndEstadoIn(paradaId, slotStartAt, BLOCKING_APPOINTMENT_STATES);
    }

    private int calculateRemainingCapacity(Parada parada, LocalDateTime slotStartAt) {
        long reservasActivas = countActiveAppointments(parada.getId(), slotStartAt);
        int capacidadRestante = Math.max(0, parada.getCapacidadMaxima() - (int) reservasActivas);
        int tecnicosDisponibles = (int) countAvailableTechnicians(slotStartAt);
        return Math.max(0, Math.min(capacidadRestante, tecnicosDisponibles));
    }

    private void validateSchedule(LocalTime horaInicio, LocalTime horaFin) {
        if (!horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio de la parada debe ser anterior a la hora de fin.");
        }
        if (horaInicio.getMinute() != 0 && horaInicio.getMinute() != 30) {
            throw new IllegalArgumentException("La hora de inicio de la parada debe ajustarse a intervalos de 30 minutos.");
        }
        if (horaFin.getMinute() != 0 && horaFin.getMinute() != 30) {
            throw new IllegalArgumentException("La hora de fin de la parada debe ajustarse a intervalos de 30 minutos.");
        }
    }

    private void validateOperationalConsistency(Ruta ruta, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (!ruta.isActiva()) {
            throw new IllegalArgumentException("No se pueden planificar paradas sobre una ruta inactiva.");
        }
        if (ruta.getTrailer() == null || !ruta.getTrailer().isActivo()) {
            throw new IllegalArgumentException("No se pueden planificar paradas sobre una ruta sin trailer activo.");
        }
        if (fecha == null || horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("La planificación de la parada debe incluir fecha y franja horaria.");
        }
        if (ruta.getTrailer() != null && ruta.getTrailer().isActivo() && horaInicio.equals(horaFin)) {
            throw new IllegalArgumentException("La parada debe tener una franja horaria operativa válida.");
        }
    }

    private void validateUniqueOrder(Long rutaId, LocalDate fecha, Integer ordenParada, Long paradaId) {
        boolean duplicatedOrder = paradaId == null
                ? paradaRepository.existsByRutaIdAndFechaAndOrdenParada(rutaId, fecha, ordenParada)
                : paradaRepository.existsByRutaIdAndFechaAndOrdenParadaAndIdNot(rutaId, fecha, ordenParada, paradaId);

        if (duplicatedOrder) {
            throw new IllegalArgumentException("Ya existe otra parada con el mismo orden en la ruta y fecha indicadas.");
        }
    }

    private void validateOverlappingStops(Long rutaId,
                                          LocalDate fecha,
                                          LocalTime horaInicio,
                                          LocalTime horaFin,
                                          Long paradaId) {
        boolean overlappingStop = paradaId == null
                ? paradaRepository.existsSolapamientoEnRutaYFecha(rutaId, fecha, horaInicio, horaFin)
                : paradaRepository.existsSolapamientoEnRutaYFechaExcluyendoId(rutaId, fecha, horaInicio, horaFin, paradaId);

        if (overlappingStop) {
            throw new IllegalArgumentException("La parada se solapa con otra parada ya planificada para la misma ruta y fecha.");
        }
    }
}
