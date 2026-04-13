package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;

import java.util.List;

public class TrailerMapper {

    public static TrailerDTO toDTO(Trailer entity) {
        if (entity == null) return null;
        return TrailerDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .activo(entity.isActivo())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public static List<TrailerDTO> toDTOList(List<Trailer> entities) {
        return entities == null ? List.of() : entities.stream().map(TrailerMapper::toDTO).toList();
    }

    public static Trailer toEntity(TrailerCreateDTO dto) {
        return Trailer.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .activo(dto.getActivo() == null || dto.getActivo())
                .descripcion(dto.getDescripcion())
                .build();
    }

    public static void copyToExistingEntity(TrailerUpdateDTO dto, Trailer entity) {
        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setActivo(dto.getActivo());
        entity.setDescripcion(dto.getDescripcion());
    }
}
