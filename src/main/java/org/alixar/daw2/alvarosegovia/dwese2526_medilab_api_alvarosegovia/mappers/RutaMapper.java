package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;

import java.util.Comparator;
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
                .tecnicoIds(toTecnicoIds(entity))
                .tecnicos(toTecnicos(entity))
                .build();
    }

    public static List<RutaDTO> toDTOList(List<Ruta> entities) {
        return entities == null ? List.of() : entities.stream().map(RutaMapper::toDTO).toList();
    }

    private static List<Long> toTecnicoIds(Ruta entity) {
        if (entity.getTecnicos() == null) {
            return List.of();
        }
        return entity.getTecnicos().stream()
                .map(User::getId)
                .sorted()
                .toList();
    }

    private static List<UserBasicDTO> toTecnicos(Ruta entity) {
        if (entity.getTecnicos() == null) {
            return List.of();
        }
        return entity.getTecnicos().stream()
                .sorted(Comparator.comparing(User::getId))
                .map(RutaMapper::toUserBasicDTO)
                .toList();
    }

    private static UserBasicDTO toUserBasicDTO(User user) {
        if (user == null) {
            return null;
        }
        UserBasicDTO dto = new UserBasicDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        if (user.getProfile() != null) {
            dto.setFirstName(user.getProfile().getFirstName());
            dto.setLastName(user.getProfile().getLastName());
        }
        return dto;
    }
}
