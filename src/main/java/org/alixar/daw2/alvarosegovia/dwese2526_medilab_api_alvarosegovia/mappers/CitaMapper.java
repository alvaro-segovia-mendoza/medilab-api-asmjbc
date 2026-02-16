package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.*;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Tecnico;

import java.util.List;

/**
 * Mapper entre la entidad {@link Cita} y sus distintos DTOs.
 * Implementación manual sin frameworks de mapeo.
 */
public class CitaMapper {

    // ------------------------------------
    // Entity -> DTO (listado)
    // ------------------------------------

    public static CitaDTO toDTO(Cita entity) {
        if (entity == null) return null;

        CitaDTO dto = new CitaDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setFechaHora(entity.getFechaHora());
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());

        if (entity.getTecnico() != null) {
            dto.setTecnicoNombre(entity.getTecnico().getNombre());
        }

        return dto;
    }

    public static List<CitaDTO> toDTOList(List<Cita> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(CitaMapper::toDTO)
                .toList();
    }

    // -----------------------------------------
    // Entity -> DTO (detalle)
    // -----------------------------------------

    public static CitaDetailDTO toDetailDTO(Cita entity) {
        if (entity == null) return null;

        CitaDetailDTO dto = new CitaDetailDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setFechaHora(entity.getFechaHora());
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());

        if (entity.getTecnico() != null) {
            dto.setTecnico(TecnicoMapper.toDTO(entity.getTecnico()));
        }

        return dto;
    }

    // -----------------------------------------
    // Entity -> DTO (edición)
    // -----------------------------------------

    public static CitaUpdateDTO toUpdateDTO(Cita entity) {
        if (entity == null) return null;

        CitaUpdateDTO dto = new CitaUpdateDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setFechaHora(entity.getFechaHora());
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());

        if (entity.getTecnico() != null) {
            dto.setTecnicoId(entity.getTecnico().getId());
        }

        return dto;
    }

    // ----------------------------------------------------
    // DTO -> Entity
    // ----------------------------------------------------

    /**
     * Crea una nueva entidad {@link Cita} a partir de un {@link CitaCreateDTO}.
     * El ID se deja a null para que lo genere la base de datos.
     */
    public static Cita toEntity(CitaCreateDTO dto) {
        if (dto == null) return null;

        Cita entity = new Cita();
        entity.setCodigo(dto.getCodigo());
        entity.setFechaHora(dto.getFechaHora());
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(dto.getEstadoCita());

        Tecnico tecnico = new Tecnico();
        tecnico.setId(dto.getTecnicoId());
        entity.setTecnico(tecnico);

        return entity;
    }

    /**
     * Crea una entidad {@link Cita} a partir de un {@link CitaUpdateDTO}.
     * Útil para updates por reemplazo completo.
     */
    public static Cita toEntity(CitaUpdateDTO dto) {
        if (dto == null) return null;

        Cita entity = new Cita();
        entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setFechaHora(dto.getFechaHora());
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(dto.getEstadoCita());

        Tecnico tecnico = new Tecnico();
        tecnico.setId(dto.getTecnicoId());
        entity.setTecnico(tecnico);

        return entity;
    }

    /**
     * Copia los campos editables desde un {@link CitaUpdateDTO}
     * a una entidad {@link Cita} ya existente.
     * Recomendado para mantener el contexto de persistencia JPA.
     */
    public static void copyToExistingEntity(CitaUpdateDTO dto, Cita entity) {
        if (dto == null || entity == null) return;

        entity.setCodigo(dto.getCodigo());
        entity.setFechaHora(dto.getFechaHora());
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(dto.getEstadoCita());

        Tecnico tecnico = new Tecnico();
        tecnico.setId(dto.getTecnicoId());
        entity.setTecnico(tecnico);
    }
}
