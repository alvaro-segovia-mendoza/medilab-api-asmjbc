package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.SlotCitaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TrailerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ParadaService paradaService;

    @Autowired
    private TrailerRepository trailerRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private SlotCitaRepository slotCitaRepository;

    @Test
    void createForcesDefaultScheduleAndGeneratesAvailableSlots() {
        Ruta ruta = createRutaActiva();

        ParadaCreateDTO dto = ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Centro de Salud Castilleja de la Cuesta")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Calle Real 1")
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 20))
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(12, 0))
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
    void updateAlsoForcesDefaultScheduleAndRegeneratesSlots() {
        Ruta ruta = createRutaActiva();

        ParadaDTO created = paradaService.create(ParadaCreateDTO.builder()
                .rutaId(ruta.getId())
                .nombre("Castilleja Inicial")
                .municipio("Castilleja de la Cuesta")
                .provincia("Sevilla")
                .direccion("Avenida Principal 2")
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 21))
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
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 21))
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(13, 0))
                .capacidadMaxima(3)
                .activa(true)
                .build());

        List<SlotCita> slots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(updated.getId());

        assertEquals(LocalTime.of(9, 0), updated.getHoraInicio());
        assertEquals(LocalTime.of(15, 0), updated.getHoraFin());
        assertEquals(36, slots.size());
        assertEquals(LocalTime.of(9, 0), slots.getFirst().getFechaHoraInicio().toLocalTime());
        assertEquals(LocalTime.of(15, 0), slots.getLast().getFechaHoraFin().toLocalTime());
        assertTrue(slots.stream().allMatch(slot -> slot.getEstado() == SlotCita.EstadoSlot.DISPONIBLE));
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
                .ordenParada(1)
                .fecha(LocalDate.of(2026, 4, 22))
                .capacidadMaxima(1)
                .activa(true)
                .build());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                paradaService.create(ParadaCreateDTO.builder()
                        .rutaId(rutaTarde.getId())
                        .nombre("Tomares")
                        .municipio("Tomares")
                        .provincia("Sevilla")
                        .direccion("Calle Dos")
                        .ordenParada(1)
                        .fecha(LocalDate.of(2026, 4, 22))
                        .capacidadMaxima(1)
                        .activa(true)
                        .build()));

        assertEquals("El trailer ya tiene una parada planificada para esa fecha.", ex.getMessage());
    }

    private Ruta createRutaActiva() {
        Trailer trailer = trailerRepository.save(Trailer.builder()
                .codigo("TRL-" + System.nanoTime())
                .nombre("Trailer operativo")
                .activo(true)
                .descripcion("Trailer de pruebas")
                .build());

        return createRutaActiva(trailer, "Ruta Sevilla Oeste");
    }

    private Ruta createRutaActiva(Trailer trailer, String routeNamePrefix) {
        return rutaRepository.save(Ruta.builder()
                .nombre(routeNamePrefix + " " + System.nanoTime())
                .origen("Sevilla")
                .destino("Castilleja de la Cuesta")
                .activa(true)
                .trailer(trailer)
                .build());
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
