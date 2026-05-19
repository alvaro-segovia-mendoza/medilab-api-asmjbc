package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.RegistroClinico;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;

import java.util.List;

/**
 * Mapper utilitario para convertir registros clinicos entre entidad y DTO.
 */
public class RegistroClinicoMapper {

    public static RegistroClinicoDTO toDTO(RegistroClinico entity) {
        if (entity == null) return null;

        RegistroClinicoDTO dto = new RegistroClinicoDTO();
        dto.setId(entity.getId());
        dto.setCitaId(entity.getCita() != null ? entity.getCita().getId() : null);
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setResultado(entity.getResultado());
        dto.setObservacionesTecnico(entity.getObservacionesTecnico());
        dto.setObservacionesMedico(entity.getObservacionesMedico());
        dto.setRecetaOSolucion(entity.getRecetaOSolucion());
        dto.setEstado(entity.getEstado());
        dto.setPaciente(toUserBasicDTO(entity.getPaciente()));
        dto.setTecnico(toUserBasicDTO(entity.getTecnico()));
        dto.setMedico(toUserBasicDTO(entity.getMedico()));
        dto.setConfirmedAt(entity.getConfirmedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static List<RegistroClinicoDTO> toDTOList(List<RegistroClinico> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(RegistroClinicoMapper::toDTO).toList();
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
