package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaReservableDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.ParadaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacion del servicio que compone la disponibilidad reservable de rutas y paradas.
 */
@Service
@Transactional(readOnly = true)
public class ReservaDisponibilidadServiceImpl implements ReservaDisponibilidadService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaDisponibilidadServiceImpl.class);

    @Autowired
    private ParadaRepository paradaRepository;

    @Autowired
    private ParadaService paradaService;

    @Override
    public List<RutaReservableDTO> listReservableRoutes(LocalDate fechaDesde) {
        LocalDate effectiveDate = fechaDesde != null ? fechaDesde : LocalDate.now();
        logger.info("Calculando rutas reservables desde fecha={}", effectiveDate);
        List<Parada> paradas = paradaRepository.findByActivaTrueAndFechaGreaterThanEqualOrderByRutaIdAscFechaAscOrdenParadaAsc(effectiveDate);
        Map<Long, RutaReservableDTO> rutas = new LinkedHashMap<>();

        for (Parada parada : paradas) {
            Ruta ruta = parada.getRuta();
            if (ruta == null || !ruta.isActiva() || ruta.getTrailer() == null || !ruta.getTrailer().isActivo()) {
                continue;
            }

            DisponibilidadParadaDTO disponibilidad = paradaService.getDisponibilidad(parada.getId());
            if (disponibilidad.getSlotsDisponibles() == null || disponibilidad.getSlotsDisponibles().isEmpty()) {
                continue;
            }

            RutaReservableDTO rutaReservable = rutas.computeIfAbsent(ruta.getId(), ignored -> RutaReservableDTO.builder()
                    .rutaId(ruta.getId())
                    .rutaNombre(ruta.getNombre())
                    .origen(ruta.getOrigen())
                    .destino(ruta.getDestino())
                    .trailerId(ruta.getTrailer().getId())
                    .trailerNombre(ruta.getTrailer().getNombre())
                    .paradas(new ArrayList<>())
                    .build());

            rutaReservable.getParadas().add(disponibilidad);
        }

        List<RutaReservableDTO> result = new ArrayList<>(rutas.values());
        logger.info("Disponibilidad reservable calculada: rutas={}", result.size());
        return result;
    }
}
