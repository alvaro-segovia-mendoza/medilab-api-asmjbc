package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;

import java.util.List;

public class RutaMapper {

    public static RutaDTO toDTO(Ruta entity) {
        if (entity == null) return null;
        return RutaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .origen(entity.getOrigen())
                .destino(entity.getDestino())
                .activa(entity.isActiva())
                .trailerId(entity.getTrailer() != null ? entity.getTrailer().getId() : null)
                .trailerNombre(entity.getTrailer() != null ? entity.getTrailer().getNombre() : null)
                .build();
    }

    public static List<RutaDTO> toDTOList(List<Ruta> entities) {
        return entities == null ? List.of() : entities.stream().map(RutaMapper::toDTO).toList();
    }
}
