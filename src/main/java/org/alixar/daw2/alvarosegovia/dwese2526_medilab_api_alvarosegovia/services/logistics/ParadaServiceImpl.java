package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.GeocodingService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.SlotDisponibilidadDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.ParadaMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.ParadaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.SlotCitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacion del servicio logistico de paradas y conciliacion de slots.
 */
@Service
@Transactional
public class ParadaServiceImpl implements ParadaService {

    private static final long CITA_DURATION_MINUTES = 30;
    private static final Logger logger = LoggerFactory.getLogger(ParadaServiceImpl.class);
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
    private CitaRepository citaRepository;

    @Autowired
    private SlotCitaRepository slotCitaRepository;

    @Autowired
    private SlotCitaGenerationService slotCitaGenerationService;

    @Autowired
    private GeocodingService geocodingService;

    @Override
    public List<ParadaDTO> list() {
        return ParadaMapper.toDTOList(paradaRepository.findAll());
    }

    @Override
    public Page<ParadaDTO> listPaged(Pageable pageable) {
        return paradaRepository.findAll(pageable).map(ParadaMapper::toDTO);
    }

    @Override
    public ParadaDTO getDetail(Long id) {
        return ParadaMapper.toDTO(getEntity(id));
    }

    @Override
    public ParadaDTO create(ParadaCreateDTO dto) {
        logger.info("Creando parada para rutaId={} en fecha={}", dto.getRutaId(), dto.getFecha());
        Ruta ruta = findRuta(dto.getRutaId());
        LocalTime horaInicio = dto.getHoraInicio();
        LocalTime horaFin = dto.getHoraFin();

        validateFechaWithinRutaCampaign(ruta, dto.getFecha());
        validateSchedule(horaInicio, horaFin);
        validateOperationalConsistency(ruta, dto.getFecha(), horaInicio, horaFin);
        validateCapacityAgainstAssignedTechnicians(ruta, dto.getCapacidadMaxima());
        validateSingleStopPerTrailerPerDay(ruta.getTrailer().getId(), dto.getFecha(), null);
        validateUniqueOrder(ruta.getId(), dto.getFecha(), dto.getOrdenParada(), null);

        BigDecimal latitud = dto.getLatitud();
        BigDecimal longitud = dto.getLongitud();
        if (latitud == null || longitud == null) {
            BigDecimal[] coords = geocodingService.geocode(dto.getDireccion(), dto.getMunicipio(), dto.getProvincia());
            latitud = coords[0];
            longitud = coords[1];
        }

        Parada parada = Parada.builder()
                .ruta(ruta)
                .nombre(dto.getNombre())
                .municipio(dto.getMunicipio())
                .provincia(dto.getProvincia())
                .direccion(dto.getDireccion())
                .latitud(latitud)
                .longitud(longitud)
                .ordenParada(dto.getOrdenParada())
                .fecha(dto.getFecha())
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .capacidadMaxima(dto.getCapacidadMaxima())
                .activa(dto.getActiva() == null || dto.getActiva())
                .build();
        parada = paradaRepository.save(parada);
        slotCitaGenerationService.reconcileSlots(parada);
        ParadaDTO created = ParadaMapper.toDTO(parada);
        logger.info("Parada creada con id={}", created.getId());
        return created;
    }

    @Override
    public ParadaDTO update(ParadaUpdateDTO dto) {
        logger.info("Actualizando parada id={}", dto.getId());
        Parada parada = getEntity(dto.getId());
        Ruta ruta = findRuta(dto.getRutaId());
        LocalTime horaInicio = dto.getHoraInicio();
        LocalTime horaFin = dto.getHoraFin();

        validateFechaWithinRutaCampaign(ruta, dto.getFecha());
        validateSchedule(horaInicio, horaFin);
        validateOperationalConsistency(ruta, dto.getFecha(), horaInicio, horaFin);
        validateCapacityAgainstAssignedTechnicians(ruta, dto.getCapacidadMaxima());
        validateSingleStopPerTrailerPerDay(ruta.getTrailer().getId(), dto.getFecha(), dto.getId());
        validateUniqueOrder(ruta.getId(), dto.getFecha(), dto.getOrdenParada(), dto.getId());
        boolean requiresSlotReconciliation = requiresSlotReconciliation(parada, dto, horaInicio, horaFin);

        if (dto.getLatitud() == null || dto.getLongitud() == null) {
            BigDecimal[] coords = geocodingService.geocode(dto.getDireccion(), dto.getMunicipio(), dto.getProvincia());
            dto.setLatitud(coords[0]);
            dto.setLongitud(coords[1]);
        }

        parada.setRuta(ruta);
        ParadaMapper.copyToExistingEntity(dto, parada);
        parada = paradaRepository.save(parada);
        if (requiresSlotReconciliation) {
            slotCitaGenerationService.reconcileSlots(parada);
        }
        ParadaDTO updated = ParadaMapper.toDTO(parada);
        logger.info("Parada actualizada con id={}", updated.getId());
        return updated;
    }

    @Override
    public void delete(Long id) {
        logger.info("Eliminando parada id={}", id);
        if (!paradaRepository.existsById(id)) {
            throw new ResourceNotFoundException("parada", "id", id);
        }
        paradaRepository.deleteById(id);
        logger.info("Parada eliminada con id={}", id);
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
        List<SlotCita> slotEntities = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(paradaId);
        Map<LocalDateTime, List<SlotCita>> slotsByStart = new LinkedHashMap<>();
        for (SlotCita slot : slotEntities) {
            slotsByStart.computeIfAbsent(slot.getFechaHoraInicio(), ignored -> new ArrayList<>()).add(slot);
        }

        List<SlotDisponibilidadDTO> slots = new ArrayList<>();
        List<LocalDateTime> slotsDisponibles = new ArrayList<>();

        for (Map.Entry<LocalDateTime, List<SlotCita>> entry : slotsByStart.entrySet()) {
            LocalDateTime slotStartAt = entry.getKey();
            List<SlotCita> slotsAtTime = entry.getValue();
            List<SlotCita> activeSlotsAtTime = slotsAtTime.stream()
                    .filter(slot -> slot.getEstado() != SlotCita.EstadoSlot.NO_DISPONIBLE)
                    .toList();
            if (activeSlotsAtTime.isEmpty()) {
                continue;
            }

            int plazasDisponibles = calculateRemainingCapacity(activeSlotsAtTime, slotStartAt, parada);
            int slotsLibres = (int) activeSlotsAtTime.stream()
                    .filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE)
                    .count();
            int tecnicosDisponibles = (int) countAvailableTechnicians(parada.getRuta(), slotStartAt);
            boolean reservable = plazasDisponibles > 0;
            List<Long> slotIdsDisponibles = reservable
                    ? activeSlotsAtTime.stream()
                    .filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE)
                    .sorted(Comparator.comparing(SlotCita::getCupoNumero))
                    .limit(plazasDisponibles)
                    .map(SlotCita::getId)
                    .toList()
                    : List.of();

            slots.add(SlotDisponibilidadDTO.builder()
                    .slotIdsDisponibles(slotIdsDisponibles)
                    .fechaHora(slotStartAt)
                    .reservasActivas((int) activeSlotsAtTime.stream().filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.RESERVADO).count())
                    .slotsLibres(slotsLibres)
                    .tecnicosDisponibles(tecnicosDisponibles)
                    .plazasDisponibles(plazasDisponibles)
                    .reservable(reservable)
                    .build());

            if (reservable) {
                slotsDisponibles.add(slotStartAt);
            }
        }

        return DisponibilidadParadaDTO.builder()
                .paradaId(parada.getId())
                .paradaNombre(parada.getNombre())
                .municipio(parada.getMunicipio())
                .provincia(parada.getProvincia())
                .direccion(parada.getDireccion())
                .latitud(parada.getLatitud())
                .longitud(parada.getLongitud())
                .ordenParada(parada.getOrdenParada())
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

    private long countAvailableTechnicians(Ruta ruta, LocalDateTime startAt) {
        if (ruta == null || ruta.getTecnicos() == null || ruta.getTecnicos().isEmpty()) {
            return 0;
        }
        LocalDateTime lowerBoundExclusive = startAt.minusMinutes(CITA_DURATION_MINUTES);
        LocalDateTime upperBoundExclusive = startAt.plusMinutes(CITA_DURATION_MINUTES);

        return ruta.getTecnicos().stream()
                .map(User::getId)
                .filter(tecnicoId -> !citaRepository.existsByTecnicoIdAndEstadoInAndSlotFechaHoraInicioAfterAndSlotFechaHoraInicioBefore(
                        tecnicoId,
                        BLOCKING_APPOINTMENT_STATES,
                        lowerBoundExclusive,
                        upperBoundExclusive
                ))
                .count();
    }

    private int calculateRemainingCapacity(List<SlotCita> slotsAtTime, LocalDateTime slotStartAt, Parada parada) {
        if (parada == null || !parada.isActiva()) {
            return 0;
        }
        int capacidadRestante = (int) slotsAtTime.stream()
                .filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE)
                .count();
        int tecnicosDisponibles = (int) countAvailableTechnicians(parada.getRuta(), slotStartAt);
        return Math.max(0, Math.min(capacidadRestante, tecnicosDisponibles));
    }

    private boolean requiresSlotReconciliation(Parada parada, ParadaUpdateDTO dto, LocalTime horaInicio, LocalTime horaFin) {
        return !parada.getFecha().equals(dto.getFecha())
                || !parada.getHoraInicio().equals(horaInicio)
                || !parada.getHoraFin().equals(horaFin)
                || !parada.getCapacidadMaxima().equals(dto.getCapacidadMaxima());
    }

    private void validateCapacityAgainstAssignedTechnicians(Ruta ruta, Integer capacidadMaxima) {
        int tecnicosAsignados = ruta.getTecnicos() == null ? 0 : ruta.getTecnicos().size();
        if (capacidadMaxima == null || capacidadMaxima > tecnicosAsignados) {
            throw ApiBusinessException.badRequest("PARADA_CAPACITY_EXCEEDS_TECNICOS", "api.error.parada.capacityExceedsTecnicos");
        }
    }

    private void validateSchedule(LocalTime horaInicio, LocalTime horaFin) {
        if (!horaInicio.isBefore(horaFin)) {
            throw ApiBusinessException.badRequest("PARADA_START_BEFORE_END", "api.error.parada.startBeforeEnd");
        }
        if (horaInicio.getMinute() != 0 && horaInicio.getMinute() != 30) {
            throw ApiBusinessException.badRequest("PARADA_START_ON_THIRTY_MINUTE_INTERVAL", "api.error.parada.startOnThirtyMinuteInterval");
        }
        if (horaFin.getMinute() != 0 && horaFin.getMinute() != 30) {
            throw ApiBusinessException.badRequest("PARADA_END_ON_THIRTY_MINUTE_INTERVAL", "api.error.parada.endOnThirtyMinuteInterval");
        }
    }

    private void validateOperationalConsistency(Ruta ruta, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (!ruta.isActiva()) {
            throw ApiBusinessException.badRequest("PARADA_RUTA_INACTIVE", "api.error.parada.rutaInactive");
        }
        if (ruta.getTrailer() == null || !ruta.getTrailer().isActivo()) {
            throw ApiBusinessException.badRequest("PARADA_RUTA_WITHOUT_ACTIVE_TRAILER", "api.error.parada.rutaWithoutActiveTrailer");
        }
        if (ruta.getTecnicos() == null || ruta.getTecnicos().isEmpty() || ruta.getTecnicos().size() > 2) {
            throw ApiBusinessException.badRequest("PARADA_RUTA_WITHOUT_TECNICOS", "api.error.parada.rutaWithoutTecnicos");
        }
        if (fecha == null || horaInicio == null || horaFin == null) {
            throw ApiBusinessException.badRequest("PARADA_SCHEDULE_REQUIRES_DATE_AND_TIME_RANGE", "api.error.parada.scheduleRequiresDateAndTimeRange");
        }
        if (ruta.getTrailer() != null && ruta.getTrailer().isActivo() && horaInicio.equals(horaFin)) {
            throw ApiBusinessException.badRequest("PARADA_INVALID_OPERATIONAL_WINDOW", "api.error.parada.invalidOperationalWindow");
        }
    }

    private void validateUniqueOrder(Long rutaId, LocalDate fecha, Integer ordenParada, Long paradaId) {
        boolean duplicatedOrder = paradaId == null
                ? paradaRepository.existsByRutaIdAndFechaAndOrdenParada(rutaId, fecha, ordenParada)
                : paradaRepository.existsByRutaIdAndFechaAndOrdenParadaAndIdNot(rutaId, fecha, ordenParada, paradaId);

        if (duplicatedOrder) {
            throw ApiBusinessException.badRequest("PARADA_DUPLICATE_ORDER_FOR_ROUTE_AND_DATE", "api.error.parada.duplicateOrderForRouteAndDate");
        }
    }

    private void validateSingleStopPerTrailerPerDay(Long trailerId, LocalDate fecha, Long paradaId) {
        boolean trailerAlreadyAssigned = paradaId == null
                ? paradaRepository.existsByRutaTrailerIdAndFecha(trailerId, fecha)
                : paradaRepository.existsByRutaTrailerIdAndFechaAndIdNot(trailerId, fecha, paradaId);

        if (trailerAlreadyAssigned) {
            throw ApiBusinessException.badRequest("PARADA_TRAILER_ALREADY_SCHEDULED_FOR_DATE", "api.error.parada.trailerAlreadyScheduledForDate");
        }
    }

    private void validateFechaWithinRutaCampaign(Ruta ruta, LocalDate fecha) {
        if (ruta.getFechaInicio() == null || ruta.getFechaFin() == null || fecha == null) {
            return;
        }
        if (fecha.isBefore(ruta.getFechaInicio()) || fecha.isAfter(ruta.getFechaFin())) {
            throw ApiBusinessException.badRequest("PARADA_FECHA_OUTSIDE_RUTA_CAMPAIGN", "api.error.parada.fechaOutsideRutaCampaign");
        }
    }
}
