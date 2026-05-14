package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.RutaMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TrailerRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RutaServiceImpl implements RutaService {

    private static final String ROLE_TECNICO = "ROLE_TECNICO";
    private static final int MIN_TECNICOS = 1;
    private static final int MAX_TECNICOS = 2;
    private static final List<Cita.EstadoCita> BLOCKING_APPOINTMENT_STATES = List.of(
            Cita.EstadoCita.PENDIENTE,
            Cita.EstadoCita.CONFIRMADA,
            Cita.EstadoCita.RESULTADOS_SUBIDOS,
            Cita.EstadoCita.RESULTADOS_APROBADOS
    );

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private TrailerRepository trailerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<RutaDTO> list() {
        return RutaMapper.toDTOList(rutaRepository.findAll());
    }

    @Override
    public Page<RutaDTO> listPaged(Pageable pageable) {
        return rutaRepository.findAll(pageable).map(RutaMapper::toDTO);
    }

    @Override
    public List<RutaDTO> listActive() {
        return RutaMapper.toDTOList(rutaRepository.findByActivaTrueOrderByNombreAsc());
    }

    @Override
    public RutaDTO getDetail(Long id) {
        return RutaMapper.toDTO(findRuta(id));
    }

    @Override
    public RutaDTO create(RutaCreateDTO dto) {
        validateRutaCampaignDates(dto.getFechaInicio(), dto.getFechaFin());
        Trailer trailer = findActiveTrailer(dto.getTrailerId());
        Set<User> tecnicos = resolveTecnicos(dto.getTecnicoIds());

        Ruta ruta = Ruta.builder()
                .nombre(dto.getNombre())
                .origen(dto.getOrigen())
                .destino(dto.getDestino())
                .activa(dto.getActiva() == null || dto.getActiva())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .descripcion(dto.getDescripcion())
                .trailer(trailer)
                .tecnicos(tecnicos)
                .build();
        return RutaMapper.toDTO(rutaRepository.save(ruta));
    }

    @Override
    public RutaDTO update(RutaUpdateDTO dto) {
        validateRutaCampaignDates(dto.getFechaInicio(), dto.getFechaFin());
        Ruta ruta = findRuta(dto.getId());
        Trailer trailer = findActiveTrailer(dto.getTrailerId());
        Set<User> tecnicos = resolveTecnicos(dto.getTecnicoIds());
        assertCanDeactivateOrChangeTrailer(ruta, dto, trailer);
        assertNotRemovingTecnicosWithActiveAppointments(ruta, tecnicos);

        ruta.setNombre(dto.getNombre());
        ruta.setOrigen(dto.getOrigen());
        ruta.setDestino(dto.getDestino());
        ruta.setActiva(dto.getActiva());
        ruta.setFechaInicio(dto.getFechaInicio());
        ruta.setFechaFin(dto.getFechaFin());
        ruta.setDescripcion(dto.getDescripcion());
        ruta.setTrailer(trailer);
        ruta.setTecnicos(tecnicos);
        return RutaMapper.toDTO(rutaRepository.save(ruta));
    }

    @Override
    public void delete(Long id) {
        if (!rutaRepository.existsById(id)) {
            throw new ResourceNotFoundException("ruta", "id", id);
        }
        rutaRepository.deleteById(id);
    }

    private Ruta findRuta(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ruta", "id", id));
    }

    private Trailer findTrailer(Long id) {
        return trailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("trailer", "id", id));
    }

    private Trailer findActiveTrailer(Long id) {
        Trailer trailer = findTrailer(id);
        if (!trailer.isActivo()) {
            throw ApiBusinessException.badRequest("RUTA_TRAILER_INACTIVE", "api.error.ruta.trailerInactive");
        }
        return trailer;
    }

    private Set<User> resolveTecnicos(List<Long> tecnicoIds) {
        if (tecnicoIds == null || tecnicoIds.size() < MIN_TECNICOS || tecnicoIds.size() > MAX_TECNICOS) {
            throw ApiBusinessException.badRequest("RUTA_TECNICOS_SIZE", "api.error.ruta.tecnicosSize");
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(tecnicoIds);
        if (uniqueIds.size() != tecnicoIds.size()) {
            throw ApiBusinessException.badRequest("RUTA_TECNICOS_DUPLICATED", "api.error.ruta.tecnicosDuplicated");
        }

        Set<User> tecnicos = uniqueIds.stream()
                .map(this::findTecnico)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (tecnicos.size() < MIN_TECNICOS || tecnicos.size() > MAX_TECNICOS) {
            throw ApiBusinessException.badRequest("RUTA_TECNICOS_SIZE", "api.error.ruta.tecnicosSize");
        }
        return tecnicos;
    }

    private User findTecnico(Long tecnicoId) {
        User user = userRepository.findWithRolesAndProfileById(tecnicoId)
                .orElseThrow(() -> new ResourceNotFoundException("tecnico", "id", tecnicoId));
        boolean isTecnico = user.getRoles() != null && user.getRoles().stream().anyMatch(role -> ROLE_TECNICO.equals(role.getName()));
        if (!isTecnico) {
            throw ApiBusinessException.badRequest("RUTA_USER_NOT_TECNICO", "api.error.ruta.userNotTecnico");
        }
        return user;
    }

    private void assertNotRemovingTecnicosWithActiveAppointments(Ruta ruta, Set<User> nextTecnicos) {
        Set<Long> nextIds = nextTecnicos.stream().map(User::getId).collect(Collectors.toSet());
        for (User currentTecnico : ruta.getTecnicos()) {
            if (!nextIds.contains(currentTecnico.getId()) && citaRepository.existsByTecnicoIdAndEstadoInAndSlotFechaHoraInicioGreaterThanEqualAndSlotParadaRutaId(
                    currentTecnico.getId(),
                    BLOCKING_APPOINTMENT_STATES,
                    LocalDateTime.now(),
                    ruta.getId())) {
                throw ApiBusinessException.badRequest("RUTA_TECNICO_HAS_ACTIVE_CITAS", "api.error.ruta.tecnicoHasActiveCitas");
            }
        }
    }

    private void assertCanDeactivateOrChangeTrailer(Ruta ruta, RutaUpdateDTO dto, Trailer nextTrailer) {
        boolean deactivating = ruta.isActiva() && Boolean.FALSE.equals(dto.getActiva());
        boolean changingTrailer = ruta.getTrailer() != null && !ruta.getTrailer().getId().equals(nextTrailer.getId());
        if ((deactivating || changingTrailer) && hasFutureActiveAppointments(ruta.getId())) {
            String code = deactivating ? "RUTA_HAS_ACTIVE_FUTURE_CITAS" : "RUTA_TRAILER_HAS_ACTIVE_FUTURE_CITAS";
            String messageKey = deactivating ? "api.error.ruta.hasActiveFutureCitas" : "api.error.ruta.trailerHasActiveFutureCitas";
            throw ApiBusinessException.badRequest(code, messageKey);
        }
    }

    private boolean hasFutureActiveAppointments(Long rutaId) {
        return citaRepository.existsByEstadoInAndSlotFechaHoraInicioGreaterThanEqualAndSlotParadaRutaId(
                BLOCKING_APPOINTMENT_STATES,
                LocalDateTime.now(),
                rutaId
        );
    }

    private void validateRutaCampaignDates(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw ApiBusinessException.badRequest("RUTA_FECHA_INICIO_AFTER_FIN", "api.error.ruta.fechaInicioAfterFin");
        }
    }
}
