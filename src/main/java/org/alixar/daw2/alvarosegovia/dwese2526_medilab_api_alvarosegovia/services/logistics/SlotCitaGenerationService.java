package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.SlotCitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class SlotCitaGenerationService {

    private static final long CITA_DURATION_MINUTES = 30;

    private final SlotCitaRepository slotCitaRepository;

    public SlotCitaGenerationService(SlotCitaRepository slotCitaRepository) {
        this.slotCitaRepository = slotCitaRepository;
    }

    public void reconcileSlots(Parada parada) {
        List<SlotCita> existingSlots = slotCitaRepository.findByParadaIdOrderByFechaHoraInicioAscCupoNumeroAsc(parada.getId());
        Map<SlotKey, SlotCita> existingByKey = new HashMap<>();
        for (SlotCita slot : existingSlots) {
            existingByKey.put(SlotKey.from(slot), slot);
        }

        List<SlotCita> slotsToSave = new ArrayList<>();
        Set<SlotKey> desiredKeys = new HashSet<>();
        LocalDateTime cursor = LocalDateTime.of(parada.getFecha(), parada.getHoraInicio());
        LocalDateTime end = LocalDateTime.of(parada.getFecha(), parada.getHoraFin());

        while (!cursor.plusMinutes(CITA_DURATION_MINUTES).isAfter(end)) {
            for (int cupo = 1; cupo <= parada.getCapacidadMaxima(); cupo++) {
                SlotKey key = new SlotKey(cursor, cupo);
                desiredKeys.add(key);
                SlotCita existing = existingByKey.get(key);
                if (existing == null) {
                    slotsToSave.add(SlotCita.builder()
                            .parada(parada)
                            .fechaHoraInicio(cursor)
                            .fechaHoraFin(cursor.plusMinutes(CITA_DURATION_MINUTES))
                            .cupoNumero(cupo)
                            .estado(SlotCita.EstadoSlot.DISPONIBLE)
                            .build());
                } else {
                    existing.setParada(parada);
                    existing.setFechaHoraFin(cursor.plusMinutes(CITA_DURATION_MINUTES));
                    if (existing.getEstado() == SlotCita.EstadoSlot.NO_DISPONIBLE) {
                        existing.setEstado(SlotCita.EstadoSlot.DISPONIBLE);
                    }
                    slotsToSave.add(existing);
                }
            }
            cursor = cursor.plusMinutes(CITA_DURATION_MINUTES);
        }

        for (SlotCita existing : existingSlots) {
            if (desiredKeys.contains(SlotKey.from(existing))) {
                continue;
            }
            if (existing.getEstado() == SlotCita.EstadoSlot.RESERVADO) {
                throw ApiBusinessException.badRequest(
                        "PARADA_CANNOT_RESCHEDULE_WITH_RESERVED_CITAS",
                        "api.error.parada.cannotRescheduleWithReservedCitas"
                );
            }
            if (existing.getEstado() != SlotCita.EstadoSlot.NO_DISPONIBLE) {
                existing.setEstado(SlotCita.EstadoSlot.NO_DISPONIBLE);
                slotsToSave.add(existing);
            }
        }

        slotCitaRepository.saveAll(slotsToSave);
    }

    private record SlotKey(LocalDateTime fechaHoraInicio, Integer cupoNumero) {
        private static SlotKey from(SlotCita slot) {
            return new SlotKey(slot.getFechaHoraInicio(), slot.getCupoNumero());
        }
    }
}
