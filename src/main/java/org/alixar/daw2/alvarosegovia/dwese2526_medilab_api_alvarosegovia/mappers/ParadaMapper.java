package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;

import java.util.List;

/**
 * Mapper utilitario para convertir entidades de parada a DTOs logistico-operativos.
 */
public class ParadaMapper {

    public static ParadaDTO toDTO(Parada entity) {
        if (entity == null) return null;
        return ParadaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .municipio(entity.getMunicipio())
                .provincia(entity.getProvincia())
                .direccion(entity.getDireccion())
                .latitud(entity.getLatitud())
                .longitud(entity.getLongitud())
                .ordenParada(entity.getOrdenParada())
                .fecha(entity.getFecha())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .capacidadMaxima(entity.getCapacidadMaxima())
                .activa(entity.isActiva())
                .rutaId(entity.getRuta() != null ? entity.getRuta().getId() : null)
                .rutaNombre(entity.getRuta() != null ? entity.getRuta().getNombre() : null)
                .trailerNombre(entity.getRuta() != null && entity.getRuta().getTrailer() != null ? entity.getRuta().getTrailer().getNombre() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static List<ParadaDTO> toDTOList(List<Parada> entities) {
        return entities == null ? List.of() : entities.stream().map(ParadaMapper::toDTO).toList();
    }

    public static ParadaBasicDTO toBasicDTO(Parada entity) {
        if (entity == null) return null;
        return ParadaBasicDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .municipio(entity.getMunicipio())
                .provincia(entity.getProvincia())
                .fecha(entity.getFecha())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .capacidadMaxima(entity.getCapacidadMaxima())
                .rutaNombre(entity.getRuta() != null ? entity.getRuta().getNombre() : null)
                .trailerNombre(entity.getRuta() != null && entity.getRuta().getTrailer() != null ? entity.getRuta().getTrailer().getNombre() : null)
                .build();
    }

    public static void copyToExistingEntity(ParadaUpdateDTO dto, Parada entity) {
        entity.setNombre(dto.getNombre());
        entity.setMunicipio(dto.getMunicipio());
        entity.setProvincia(dto.getProvincia());
        entity.setDireccion(dto.getDireccion());
        entity.setLatitud(dto.getLatitud());
        entity.setLongitud(dto.getLongitud());
        entity.setOrdenParada(dto.getOrdenParada());
        entity.setFecha(dto.getFecha());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        entity.setCapacidadMaxima(dto.getCapacidadMaxima());
        entity.setActiva(dto.getActiva());
    }
}
