package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Role;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RoleRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.SlotCitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TrailerRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ParadaServiceImplTest {

    private static final BigDecimal DEFAULT_LATITUD = new BigDecimal("37.386356");
    private static final BigDecimal DEFAULT_LONGITUD = new BigDecimal("-6.051731");
    private static final LocalTime DEFAULT_HORA_INICIO = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_HORA_FIN = LocalTime.of(15, 0);

    @Autowired
    private ParadaService paradaService;

    @Autowired
    private RutaService rutaService;

    @Autowired
    private TrailerRepository trailerRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private SlotCitaRepository slotCitaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createWithUserSuppliedScheduleGeneratesCorrectSlots() {
        Ruta ruta = createRutaActiva();

        ParadaCreateDTO dto = ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Centro de Salud Castilleja de la Cuesta")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Calle Real 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 20))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(true)
                .build();

        ParadaDTO created = paradaService.create(dto);
        List<SlotCita> slots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(created.getId());

        assertEquals(LocalTime.of(9, 0), created.getHoraInicio());
        assertEquals(LocalTime.of(15, 0), created.getHoraFin());
        assertEquals(24, slots.size());
        assertEquals(LocalTime.of(9, 0), slots.getFirst().getFechaHoraInicio().toLocalTime());
        assertEquals(LocalTime.of(15, 0), slots.getLast().getFechaHoraFin().toLocalTime());
        assertTrue(slots.stream().allMatch(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE));
    }

    @Test
    void updateDecreaseCapacityMarksExtraSlotsNoDisponibleAndHidesThemFromAvailability() {
        Ruta ruta = createRutaActiva();

        ParadaDTO created = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja con cupos")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Cupos 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 26))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(true)
                .build());

        ParadaDTO updated = paradaService.update(ParadaUpdateDTO.builder()
                .id(created.getId())
                .rutaId(ruta.getId())
                .nombre("Castilleja con menos cupos")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Cupos 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 26))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(1)
                .activa(true)
                .build());

        List<SlotCita> slots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(updated.getId());
        DisponibilidadParadaDTO disponibilidad = paradaService.getDisponibilidad(updated.getId());

        assertEquals(24, slots.size());
        assertEquals(12, slots.stream().filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE).count());
        assertEquals(12, slots.stream().filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.NO_DISPONIBLE).count());
        assertEquals(12, disponibilidad.getSlots().size());
        assertTrue(disponibilidad.getSlots().stream().allMatch(slot -> slot.getSlotIdsDisponibles().size() == 1));
    }

    @Test
    void updateIncreaseCapacityReactivatesPreviouslyDisabledSlots() {
        Ruta ruta = createRutaActiva();

        ParadaDTO created = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja variable")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Variable 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 27))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(true)
                .build());

        paradaService.update(ParadaUpdateDTO.builder()
                .id(created.getId())
                .rutaId(ruta.getId())
                .nombre("Castilleja reducida")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Variable 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 27))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(1)
                .activa(true)
                .build());

        ParadaDTO increased = paradaService.update(ParadaUpdateDTO.builder()
                .id(created.getId())
                .rutaId(ruta.getId())
                .nombre("Castilleja ampliada")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Variable 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 27))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(true)
                .build());

        List<SlotCita> slots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(increased.getId());

        assertEquals(24, slots.size());
        assertEquals(24, slots.stream().filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE).count());
        assertEquals(0, slots.stream().filter(slot -> slot.getEstado() == SlotCita.EstadoSlot.NO_DISPONIBLE).count());
    }

    @Test
    void updateRejectsCapacityReductionThatWouldRemoveReservedSlot() {
        Ruta ruta = createRutaActiva();

        ParadaDTO created = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja reservada")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Reservas 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 28))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(true)
                .build());

        SlotCita secondCupFirstSlot = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(created.getId()).get(1);
        secondCupFirstSlot.setEstado(SlotCita.EstadoSlot.RESERVADO);
        slotCitaRepository.save(secondCupFirstSlot);

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                paradaService.update(ParadaUpdateDTO.builder()
                        .id(created.getId())
                        .rutaId(ruta.getId())
                        .nombre("Castilleja reduccion bloqueada")
                        .municipio("Castilleja de la Cuesta")
                        .provincia("Sevilla")
                        .direccion("Avenida Reservas 1")
                        .latitud(DEFAULT_LATITUD)
                        .longitud(DEFAULT_LONGITUD)
                        .ordenParada(1)
                        .fecha(LocalDate.of(2026, 4, 28))
                        .horaInicio(DEFAULT_HORA_INICIO)
                        .horaFin(DEFAULT_HORA_FIN)
                        .capacidadMaxima(1)
                        .activa(true)
                        .build()));

        assertEquals("PARADA_CANNOT_RESCHEDULE_WITH_RESERVED_CITAS", ex.getCode());
    }

    @Test
    void updateWithoutScheduleOrCapacityChangesKeepsExistingSlotsUntouched() {
        Ruta ruta = createRutaActiva();

        ParadaDTO created = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja estable")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Estable 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 29))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(true)
                .build());

        List<SlotCita> originalSlots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(created.getId());
        List<Long> originalSlotIds = originalSlots.stream().map(SlotCita::getId).toList();

        ParadaDTO updated = paradaService.update(ParadaUpdateDTO.builder()
                .id(created.getId())
                .rutaId(ruta.getId())
                .nombre("Castilleja estable actualizada")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Estable 2")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 29))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(false)
                .build());

        List<SlotCita> updatedSlots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(updated.getId());

        assertEquals(24, updatedSlots.size());
        assertEquals(originalSlotIds, updatedSlots.stream().map(SlotCita::getId).toList());
        assertEquals(LocalTime.of(9, 0), updatedSlots.getFirst().getFechaHoraInicio().toLocalTime());
        assertEquals(LocalTime.of(15, 0), updatedSlots.getLast().getFechaHoraFin().toLocalTime());
        assertTrue(updatedSlots.stream().allMatch(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE));
    }

    @Test
    void updateWithNewScheduleRegeneratesSlots() {
        Ruta ruta = createRutaActiva();

        ParadaDTO created = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja Inicial")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Principal 2")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 21))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(1)
                .activa(true)
                .build());

        ParadaDTO updated = paradaService.update(ParadaUpdateDTO.builder()
                .id(created.getId())
                .rutaId(ruta.getId())
                .nombre("Castilleja Actualizada")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Principal 3")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 21))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(2)
                .activa(true)
                .build());

        List<SlotCita> slots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(updated.getId());

        assertEquals(LocalTime.of(9, 0), updated.getHoraInicio());
        assertEquals(LocalTime.of(15, 0), updated.getHoraFin());
        assertEquals(24, slots.size());
        assertEquals(LocalTime.of(9, 0), slots.getFirst().getFechaHoraInicio().toLocalTime());
        assertEquals(LocalTime.of(15, 0), slots.getLast().getFechaHoraFin().toLocalTime());
        assertTrue(slots.stream().allMatch(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE));
    }

    @Test
    void createRejectsCapacityGreaterThanAssignedTechnicians() {
        Ruta ruta = createRutaActiva(1);

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                paradaService.create(ParadaCreateDTO.builder()
                        .rutaId(ruta.getId())
                        .nombre("Castilleja capacidad invalida")
                        .municipio("Castilleja de la Cuesta")
                        .provincia("Sevilla")
                        .direccion("Calle Capacidad 1")
                        .latitud(DEFAULT_LATITUD)
                        .longitud(DEFAULT_LONGITUD)
                        .ordenParada(1)
                        .fecha(LocalDate.of(2026, 5, 20))
                        .horaInicio(DEFAULT_HORA_INICIO)
                        .horaFin(DEFAULT_HORA_FIN)
                        .capacidadMaxima(2)
                        .activa(true)
                        .build()));

        assertEquals("PARADA_CAPACITY_EXCEEDS_TECNICOS", ex.getCode());
    }

    @Test
    void updateRejectsCapacityGreaterThanAssignedTechnicians() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO created = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja capacidad")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Calle Capacidad 2")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 5, 21))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(1)
                .activa(true)
                .build());

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                paradaService.update(ParadaUpdateDTO.builder()
                        .id(created.getId())
                        .rutaId(ruta.getId())
                        .nombre("Castilleja capacidad actualizada")
                        .municipio("Castilleja de la Cuesta")
                        .provincia("Sevilla")
                        .direccion("Calle Capacidad 2")
                        .latitud(DEFAULT_LATITUD)
                        .longitud(DEFAULT_LONGITUD)
                        .ordenParada(1)
                        .fecha(LocalDate.of(2026, 5, 21))
                        .horaInicio(DEFAULT_HORA_INICIO)
                        .horaFin(DEFAULT_HORA_FIN)
                        .capacidadMaxima(2)
                        .activa(true)
                        .build()));

        assertEquals("PARADA_CAPACITY_EXCEEDS_TECNICOS", ex.getCode());
    }

    @Test
    void updateRouteRejectsDeactivationWithFutureActiveAppointments() {
        Ruta ruta = createRutaActiva();
        ParadaDTO parada = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja futura")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Calle Futuro 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 6, 20))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(1)
                .activa(true)
                .build());
        SlotCita slot = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(parada.getId()).getFirst();
        slot.setEstado(SlotCita.EstadoSlot.RESERVADO);
        slot = slotCitaRepository.save(slot);
        User tecnico = ruta.getTecnicos().iterator().next();
        User paciente = createUserWithRole("ROLE_PACIENTE", "Paciente", "Usuario paciente");
        jdbcTemplate.update("""
                        insert into cita (tipo_prueba, estado, paciente_id, tecnico_id, slot_id, created_at, updated_at)
                        values (?, ?, ?, ?, ?, current_timestamp, current_timestamp)
                        """,
                "Analítica",
                "PENDIENTE",
                paciente.getId(),
                tecnico.getId(),
                slot.getId());

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                rutaService.update(RutaUpdateDTO.builder()
                        .id(ruta.getId())
                        .nombre(ruta.getNombre())
                        .origen(ruta.getOrigen())
                        .destino(ruta.getDestino())
                        .activa(false)
                        .fechaInicio(ruta.getFechaInicio())
                        .fechaFin(ruta.getFechaFin())
                        .trailerId(ruta.getTrailer().getId())
                        .tecnicoIds(ruta.getTecnicos().stream().map(User::getId).toList())
                        .build()));

        assertEquals("RUTA_HAS_ACTIVE_FUTURE_CITAS", ex.getCode());
    }

    @Test
    void createRejectsParadaFechaOutsideCampaign() {
        Ruta ruta = createRutaActivaWithCampaign(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                paradaService.create(ParadaCreateDTO.builder()
                        .rutaId(ruta.getId())
                        .nombre("Castilleja fuera de campaña")
                        .municipio("Castilleja de la Cuesta")
                        .provincia("Sevilla")
                        .direccion("Calle Campaña 1")
                        .latitud(DEFAULT_LATITUD)
                        .longitud(DEFAULT_LONGITUD)
                        .ordenParada(1)
                        .fecha(LocalDate.of(2026, 5, 1))
                        .horaInicio(DEFAULT_HORA_INICIO)
                        .horaFin(DEFAULT_HORA_FIN)
                        .capacidadMaxima(1)
                        .activa(true)
                        .build()));

        assertEquals("PARADA_FECHA_OUTSIDE_RUTA_CAMPAIGN", ex.getCode());
    }

    @Test
    void createRejectsSecondStopOnSameDayForSameTrailerEvenAcrossDifferentRoutes() {
        Trailer trailer = trailerRepository.save(Trailer.builder()
                .codigo("TRL-" + System.nanoTime())
                .nombre("Trailer compartido")
                .activo(true)
                .descripcion("Trailer comun para varias rutas")
                .build());

        Ruta rutaManana = createRutaActiva(trailer, "Ruta manana");
        Ruta rutaTarde = createRutaActiva(trailer, "Ruta tarde");

        paradaService.create(ParadaCreateDTO.builder()
                .rutaId(rutaManana.getId())
                .nombre("Castilleja")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Calle Uno")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 22))
                .horaInicio(DEFAULT_HORA_INICIO)
                .horaFin(DEFAULT_HORA_FIN)
                .capacidadMaxima(1)
                .activa(true)
                .build());

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                paradaService.create(ParadaCreateDTO.builder()
                        .rutaId(rutaTarde.getId())
                        .nombre("Tomares")
                        .municipio("Tomares")
                        .provincia("Sevilla")
                        .direccion("Calle Dos")
                        .latitud(new BigDecimal("37.372000"))
                        .longitud(new BigDecimal("-6.071000"))
                        .ordenParada(1)
                        .fecha(LocalDate.of(2026, 4, 22))
                        .horaInicio(DEFAULT_HORA_INICIO)
                        .horaFin(DEFAULT_HORA_FIN)
                        .capacidadMaxima(1)
                        .activa(true)
                        .build()));

        assertEquals("PARADA_TRAILER_ALREADY_SCHEDULED_FOR_DATE", ex.getCode());
        assertEquals("api.error.parada.trailerAlreadyScheduledForDate", ex.getMessageKey());
    }

    private Ruta createRutaActiva() {
        return createRutaActiva(2);
    }

    private Ruta createRutaActiva(int tecnicoCount) {
        Trailer trailer = trailerRepository.save(Trailer.builder()
                .codigo("TRL-" + System.nanoTime())
                .nombre("Trailer operativo")
                .activo(true)
                .descripcion("Trailer de pruebas")
                .build());

        return createRutaActiva(trailer, "Ruta Sevilla Oeste", tecnicoCount);
    }

    private Ruta createRutaActiva(Trailer trailer, String routeNamePrefix) {
        return createRutaActiva(trailer, routeNamePrefix, 2);
    }

    private Ruta createRutaActiva(Trailer trailer, String routeNamePrefix, int tecnicoCount) {
        List<User> tecnicos = java.util.stream.IntStream.range(0, tecnicoCount)
                .mapToObj(ignored -> createTecnico())
                .toList();
        return rutaRepository.save(Ruta.builder()
                .nombre(routeNamePrefix + " " + System.nanoTime())
                .origen("Sevilla")
                .destino("Castilleja de la Cuesta")
                .activa(true)
                .fechaInicio(LocalDate.of(2026, 1, 1))
                .fechaFin(LocalDate.of(2027, 12, 31))
                .trailer(trailer)
                .tecnicos(new java.util.LinkedHashSet<>(tecnicos))
                .build());
    }

    private Ruta createRutaActivaWithCampaign(LocalDate fechaInicio, LocalDate fechaFin) {
        Trailer trailer = trailerRepository.save(Trailer.builder()
                .codigo("TRL-" + System.nanoTime())
                .nombre("Trailer campaña")
                .activo(true)
                .descripcion("Trailer de pruebas campaña")
                .build());
        List<User> tecnicos = List.of(createTecnico());
        return rutaRepository.save(Ruta.builder()
                .nombre("Ruta Campaña " + System.nanoTime())
                .origen("Sevilla")
                .destino("Granada")
                .activa(true)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .trailer(trailer)
                .tecnicos(new java.util.LinkedHashSet<>(tecnicos))
                .build());
    }

    private User createTecnico() {
        return createUserWithRole("ROLE_TECNICO", "Tecnico", "Usuario técnico");
    }

    private User createUserWithRole(String roleName, String displayName, String description) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName, displayName, description)));
        User user = new User(
                roleName.toLowerCase().replace("role_", "") + "-" + System.nanoTime() + "@app.local",
                "$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq",
                true,
                true,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusMonths(3),
                0,
                true,
                false
        );
        user.setRoles(new java.util.LinkedHashSet<>(List.of(role)));
        return userRepository.save(user);
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean(name = "jwtKeyPair")
        @Primary
        KeyPair jwtKeyPair() throws Exception {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }
    }
}
