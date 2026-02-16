package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.*;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Tecnico;

import java.util.List;

/**
 * Mapper para convertir entre entidades {@link Tecnico} y sus DTOs.
 * Implementación manual sin frameworks de mapeo.
 */
public class TecnicoMapper {

    // ----------------------------------------------------------
    // Entity -> DTO (listado básico)
    // ----------------------------------------------------------

    /**
     * Convierte una entidad {@link Tecnico} a un {@link TecnicoDTO}.
     */
    public static TecnicoDTO toDTO(Tecnico entity) {
        if (entity == null) return null;

        TecnicoDTO dto = new TecnicoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellidos(entity.getApellidos());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setActivo(entity.isActivo());

        dto.setDni(entity.getDni());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setDireccion(entity.getDireccion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setProvincia(entity.getProvincia());

        return dto;
    }

    /**
     * Convierte una lista de entidades {@link Tecnico} a una lista de {@link TecnicoDTO}.
     */
    public static List<TecnicoDTO> toDTOList(List<Tecnico> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(TecnicoMapper::toDTO).toList();
    }

    // ----------------------------------------------------------
    // Entity -> DTO (detalle completo)
    // ----------------------------------------------------------

    /**
     * Convierte un {@link Tecnico} a {@link TecnicoDetailDTO}, incluyendo sus citas.
     */
    public static TecnicoDetailDTO toDetailDTO(Tecnico entity) {
        if (entity == null) return null;

        TecnicoDetailDTO dto = new TecnicoDetailDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellidos(entity.getApellidos());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setActivo(entity.isActivo());
        dto.setDni(entity.getDni());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setDireccion(entity.getDireccion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setProvincia(entity.getProvincia());
        dto.setCitas(toCitaList(entity.getCitas()));

        return dto;
    }

    // ----------------------------------------------------------
    // Citas (Entity -> DTO)
    // ----------------------------------------------------------

    private static CitaDTO toCitaDTO(Cita c) {
        if (c == null) return null;

        CitaDTO dto = new CitaDTO();
        dto.setId(c.getId());
        dto.setCodigo(c.getCodigo());
        dto.setFechaHora(c.getFechaHora());
        dto.setEstadoCita(c.getEstado());
        dto.setTipoPrueba(c.getTipoPrueba());

        return dto;
    }

    /**
     * Convierte una lista de {@link Cita} a una lista de {@link CitaDTO}.
     */
    public static List<CitaDTO> toCitaList(List<Cita> citas) {
        if (citas == null) return List.of();
        return citas.stream().map(TecnicoMapper::toCitaDTO).toList();
    }

    // ----------------------------------------------------------
    // DTO -> DTO (update form)
    // ----------------------------------------------------------

    /**
     * Convierte un {@link Tecnico} a un {@link TecnicoUpdateDTO}.
     */
    public static TecnicoUpdateDTO toUpdateDTO(Tecnico entity) {
        if (entity == null) return null;

        TecnicoUpdateDTO dto = new TecnicoUpdateDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellidos(entity.getApellidos());
        dto.setEmail(entity.getEmail());
        dto.setContrasena(entity.getContrasena());
        dto.setTelefono(entity.getTelefono());
        dto.setActivo(entity.isActivo());
        dto.setDni(entity.getDni());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setDireccion(entity.getDireccion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setProvincia(entity.getProvincia());

        return dto;
    }

    // ----------------------------------------------------------
    // DTO -> Entity (create / update)
    // ----------------------------------------------------------

    /**
     * Crea una nueva entidad {@link Tecnico} desde un {@link TecnicoCreateDTO}.
     */
    public static Tecnico toEntity(TecnicoCreateDTO dto) {
        if (dto == null) return null;

        Tecnico entity = new Tecnico();
        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setEmail(dto.getEmail());
        entity.setContrasena(dto.getContrasena());
        entity.setTelefono(dto.getTelefono());
        entity.setActivo(dto.isActivo());
        entity.setDni(dto.getDni());
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setDireccion(dto.getDireccion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setProvincia(dto.getProvincia());

        return entity;
    }

    /**
     * Convierte un {@link TecnicoUpdateDTO} en una entidad nueva.
     * Usar solo si haces update por reemplazo completo.
     */
    public static Tecnico toEntity(TecnicoUpdateDTO dto) {
        if (dto == null) return null;

        Tecnico entity = new Tecnico();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setEmail(dto.getEmail());
        entity.setContrasena(dto.getContrasena());
        entity.setTelefono(dto.getTelefono());
        entity.setActivo(dto.isActivo());
        entity.setDni(dto.getDni());
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setDireccion(dto.getDireccion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setProvincia(dto.getProvincia());

        return entity;
    }

    /**
     * Copia los datos modificables de un DTO de actualización a una entidad existente.
     */
    public static void copyToExistingEntity(TecnicoUpdateDTO dto, Tecnico entity) {
        if (dto == null || entity == null) return;

        entity.setNombre(dto.getNombre());
        entity.setApellidos(dto.getApellidos());
        entity.setEmail(dto.getEmail());
        entity.setContrasena(dto.getContrasena());
        entity.setTelefono(dto.getTelefono());
        entity.setActivo(dto.isActivo());
        entity.setDni(dto.getDni());
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setDireccion(dto.getDireccion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setProvincia(dto.getProvincia());
    }
}
