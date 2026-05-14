package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Role;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RoleRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.SlotCitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TrailerRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics.ParadaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CitaServiceImplTest {

    private static final BigDecimal DEFAULT_LATITUD = new BigDecimal("37.386356");
    private static final BigDecimal DEFAULT_LONGITUD = new BigDecimal("-6.051731");

    @Autowired
    private CitaService citaService;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private SlotCitaRepository slotCitaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TrailerRepository trailerRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private ParadaService paradaService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    @Test
    void createAssignsTechnicianAndMarksSlotReservado() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO parada = createFutureParada(ruta, 1);
        SlotCita slot = firstDisponibleSlot(parada);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        CitaDTO result = citaService.create(CitaCreateDTO.builder()
                .tipoPrueba("Analítica")
                .pacienteId(paciente.getId())
                .slotId(slot.getId())
                .build());

        assertNotNull(result.getId());
        assertNotNull(result.getTecnicoNombre());

        SlotCita updated = slotCitaRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotCita.EstadoSlot.RESERVADO, updated.getEstado());
    }

    @Test
    void createRejectsPastSlot() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO parada = createPastParada(ruta, 1);
        SlotCita slot = firstDisponibleSlot(parada);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                citaService.create(CitaCreateDTO.builder()
                        .tipoPrueba("Analítica")
                        .pacienteId(paciente.getId())
                        .slotId(slot.getId())
                        .build()));

        assertEquals("CITA_SLOT_IN_PAST", ex.getCode());
    }

    @Test
    void createRejectsWhenNoTechnicianAvailableForFranja() {
        // Two technicians, two cupos. Manually occupy both technicians in the target
        // franja, then attempt to book → no tech available.
        Ruta ruta = createRutaActiva(2);
        ParadaDTO parada = createFutureParada(ruta, 2);
        List<SlotCita> slots = slotCitaRepository
                .findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(parada.getId());
        // cupo1 and cupo2 for the 09:00 franja
        SlotCita cupo1 = slots.get(0);
        SlotCita cupo2 = slots.get(1);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        // Occupy tech1 in cupo1
        citaService.create(CitaCreateDTO.builder()
                .tipoPrueba("Analítica")
                .pacienteId(paciente.getId())
                .slotId(cupo1.getId())
                .build());

        // Occupy tech2 via direct insert so both techs are now busy
        User paciente2 = createUserWithRole("ROLE_PACIENTE");
        List<User> tecnicos = new java.util.ArrayList<>(ruta.getTecnicos());
        // Find the tech not yet assigned to cupo1's cita
        Cita citaCupo1 = citaRepository.findAll().stream()
                .filter(c -> c.getSlot().getId().equals(cupo1.getId()))
                .findFirst().orElseThrow();
        User tech2 = tecnicos.stream()
                .filter(t -> !t.getId().equals(citaCupo1.getTecnico().getId()))
                .findFirst().orElseThrow();

        jdbcTemplate.update("""
                INSERT INTO cita (tipo_prueba, estado, paciente_id, tecnico_id, slot_id, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, current_timestamp, current_timestamp)
                """,
                "Radiografía", "PENDIENTE", paciente2.getId(), tech2.getId(), cupo2.getId());
        // Mark cupo2 RESERVADO so it passes slot-state check (skipped since this insert bypasses service,
        // but we're testing tech unavailability — use a different slot for the 2nd create attempt)
        // Instead: book cupo1 already consumed tech1. Use jdbcTemplate to mark tech2 busy on cupo2
        // and then try to book cupo2 expecting no tech available.
        // Revert: restore cupo2 to DISPONIBLE so service sees it as bookable, but both techs are occupied.
        cupo2.setEstado(SlotCita.EstadoSlot.DISPONIBLE);
        slotCitaRepository.save(cupo2);

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                citaService.create(CitaCreateDTO.builder()
                        .tipoPrueba("Glucosa")
                        .pacienteId(paciente.getId())
                        .slotId(cupo2.getId())
                        .build()));

        assertEquals("CITA_NO_TECNICO_AVAILABLE", ex.getCode());
    }

    @Test
    void updateWithCanceladaReleasesSlot() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO parada = createFutureParada(ruta, 1);
        SlotCita slot = firstDisponibleSlot(parada);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        CitaDTO created = citaService.create(CitaCreateDTO.builder()
                .tipoPrueba("Analítica")
                .pacienteId(paciente.getId())
                .slotId(slot.getId())
                .build());

        citaService.update(CitaUpdateDTO.builder()
                .id(created.getId())
                .tipoPrueba(created.getTipoPrueba())
                .estadoCita(Cita.EstadoCita.CANCELADA)
                .pacienteId(paciente.getId())
                .slotId(slot.getId())
                .build());

        SlotCita updated = slotCitaRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotCita.EstadoSlot.DISPONIBLE, updated.getEstado());

        Cita saved = citaRepository.findById(created.getId()).orElseThrow();
        assertEquals(Cita.EstadoCita.CANCELADA, saved.getEstado());
    }

    @Test
    void updateAlreadyCancelledCitaThrowsError() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO parada = createFutureParada(ruta, 1);
        SlotCita slot = firstDisponibleSlot(parada);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        CitaDTO created = citaService.create(CitaCreateDTO.builder()
                .tipoPrueba("Analítica")
                .pacienteId(paciente.getId())
                .slotId(slot.getId())
                .build());
        citaService.cancel(created.getId());

        ApiBusinessException ex = assertThrows(ApiBusinessException.class, () ->
                citaService.update(CitaUpdateDTO.builder()
                        .id(created.getId())
                        .tipoPrueba("Radiografía")
                        .estadoCita(Cita.EstadoCita.CONFIRMADA)
                        .pacienteId(paciente.getId())
                        .slotId(slot.getId())
                        .build()));

        assertEquals("CITA_ALREADY_CANCELLED", ex.getCode());
    }

    @Test
    void cancelReleasesSlot() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO parada = createFutureParada(ruta, 1);
        SlotCita slot = firstDisponibleSlot(parada);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        CitaDTO created = citaService.create(CitaCreateDTO.builder()
                .tipoPrueba("Analítica")
                .pacienteId(paciente.getId())
                .slotId(slot.getId())
                .build());

        citaService.cancel(created.getId());

        SlotCita updated = slotCitaRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotCita.EstadoSlot.DISPONIBLE, updated.getEstado());
    }

    @Test
    void deleteActiveCitaReleasesSlot() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO parada = createFutureParada(ruta, 1);
        SlotCita slot = firstDisponibleSlot(parada);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        CitaDTO created = citaService.create(CitaCreateDTO.builder()
                .tipoPrueba("Analítica")
                .pacienteId(paciente.getId())
                .slotId(slot.getId())
                .build());

        citaService.delete(created.getId());

        SlotCita updated = slotCitaRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotCita.EstadoSlot.DISPONIBLE, updated.getEstado());
    }

    @Test
    void deleteCancelledCitaDoesNotReopenNoDisponibleSlot() {
        Ruta ruta = createRutaActiva(1);
        ParadaDTO parada = createFutureParada(ruta, 1);
        SlotCita slot = firstDisponibleSlot(parada);

        User admin = createUserWithRole("ROLE_ADMIN");
        User paciente = createUserWithRole("ROLE_PACIENTE");
        loginAs(admin);

        CitaDTO created = citaService.create(CitaCreateDTO.builder()
                .tipoPrueba("Analítica")
                .pacienteId(paciente.getId())
                .slotId(slot.getId())
                .build());

        // Cancel: releases slot → DISPONIBLE
        citaService.cancel(created.getId());

        // Simulate reconcile marking slot NO_DISPONIBLE
        SlotCita s = slotCitaRepository.findById(slot.getId()).orElseThrow();
        s.setEstado(SlotCita.EstadoSlot.NO_DISPONIBLE);
        slotCitaRepository.save(s);

        // Delete the already-cancelled cita — must NOT touch NO_DISPONIBLE slot
        citaService.delete(created.getId());

        SlotCita after = slotCitaRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotCita.EstadoSlot.NO_DISPONIBLE, after.getEstado());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void loginAs(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getName()))
                                .toList()
                ));
    }

    private Ruta createRutaActiva(int tecnicoCount) {
        Trailer trailer = trailerRepository.save(Trailer.builder()
                .codigo("TRL-" + System.nanoTime())
                .nombre("Trailer test")
                .activo(true)
                .descripcion("Trailer de pruebas")
                .build());

        List<User> tecnicos = java.util.stream.IntStream.range(0, tecnicoCount)
                .mapToObj(i -> createUserWithRole("ROLE_TECNICO"))
                .toList();

        return rutaRepository.save(Ruta.builder()
                .nombre("Ruta test " + System.nanoTime())
                .origen("Sevilla")
                .destino("Castilleja")
                .activa(true)
                .fechaInicio(LocalDate.of(2023, 1, 1))
                .fechaFin(LocalDate.of(2030, 12, 31))
                .trailer(trailer)
                .tecnicos(new LinkedHashSet<>(tecnicos))
                .build());
    }

    private ParadaDTO createFutureParada(Ruta ruta, int capacidad) {
        return paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Parada test " + System.nanoTime())
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Calle Test 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2027, 6, 1))
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(15, 0))
                .capacidadMaxima(capacidad)
                .activa(true)
                .build());
    }

    private ParadaDTO createPastParada(Ruta ruta, int capacidad) {
        return paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Parada pasada " + System.nanoTime())
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Calle Pasado 1")
                .latitud(DEFAULT_LATITUD)
                .longitud(DEFAULT_LONGITUD)
                .ordenParada(1)
                .fecha(LocalDate.of(2024, 1, 15))
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(15, 0))
                .capacidadMaxima(capacidad)
                .activa(true)
                .build());
    }

    private SlotCita firstDisponibleSlot(ParadaDTO parada) {
        return slotCitaRepository
                .findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(parada.getId())
                .stream()
                .filter(s -> s.getEstado() == SlotCita.EstadoSlot.DISPONIBLE)
                .findFirst()
                .orElseThrow();
    }

    private User createUserWithRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName, roleName, roleName)));
        User user = new User(
                roleName.toLowerCase().replace("role_", "") + "-" + System.nanoTime() + "@test.local",
                "$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq",
                true,
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3),
                0,
                true,
                false
        );
        user.setRoles(new LinkedHashSet<>(List.of(role)));
        return userRepository.save(user);
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean(name = "jwtKeyPair")
        @Primary
        KeyPair jwtKeyPair() throws Exception {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            return gen.generateKeyPair();
        }
    }
}
