package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.*;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;

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
        dto.setFechaHora(entity.getFechaHora());
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());
        dto.setPacienteNombre(getDisplayName(entity.getPaciente()));
        dto.setTecnicoNombre(getDisplayName(entity.getTecnico()));
        dto.setDoctorNombre(getDisplayName(entity.getDoctor()));
        dto.setParadaNombre(entity.getParada() != null ? entity.getParada().getNombre() : null);
        dto.setMunicipioParada(entity.getParada() != null ? entity.getParada().getMunicipio() : null);
        dto.setRutaNombre(entity.getParada() != null && entity.getParada().getRuta() != null ? entity.getParada().getRuta().getNombre() : null);
        dto.setTrailerNombre(entity.getParada() != null && entity.getParada().getRuta() != null && entity.getParada().getRuta().getTrailer() != null ? entity.getParada().getRuta().getTrailer().getNombre() : null);

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
        dto.setFechaHora(entity.getFechaHora());
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());
        dto.setPaciente(toUserBasicDTO(entity.getPaciente()));
        dto.setTecnico(toUserBasicDTO(entity.getTecnico()));
        dto.setDoctor(toUserBasicDTO(entity.getDoctor()));
        dto.setParada(ParadaMapper.toBasicDTO(entity.getParada()));
        dto.setRutaNombre(entity.getParada() != null && entity.getParada().getRuta() != null ? entity.getParada().getRuta().getNombre() : null);
        dto.setTrailerNombre(entity.getParada() != null && entity.getParada().getRuta() != null && entity.getParada().getRuta().getTrailer() != null ? entity.getParada().getRuta().getTrailer().getNombre() : null);

        return dto;
    }

    // -----------------------------------------
    // Entity -> DTO (edición)
    // -----------------------------------------

    public static CitaUpdateDTO toUpdateDTO(Cita entity) {
        if (entity == null) return null;

        CitaUpdateDTO dto = new CitaUpdateDTO();
        dto.setId(entity.getId());
        dto.setFechaHora(entity.getFechaHora());
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());
        dto.setPacienteId(getUserId(entity.getPaciente()));
        dto.setParadaId(getParadaId(entity.getParada()));
        dto.setTecnicoId(getUserId(entity.getTecnico()));
        dto.setDoctorId(getUserId(entity.getDoctor()));

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
        entity.setFechaHora(dto.getFechaHora());
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(Cita.EstadoCita.PENDIENTE);
        entity.setPaciente(toUserReference(dto.getPacienteId()));
        entity.setParada(toParadaReference(dto.getParadaId()));

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
        entity.setFechaHora(dto.getFechaHora());
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(dto.getEstadoCita());
        entity.setPaciente(toUserReference(dto.getPacienteId()));
        entity.setParada(toParadaReference(dto.getParadaId()));
        entity.setTecnico(toUserReference(dto.getTecnicoId()));
        entity.setDoctor(toUserReference(dto.getDoctorId()));

        return entity;
    }

    /**
     * Copia los campos editables desde un {@link CitaUpdateDTO}
     * a una entidad {@link Cita} ya existente.
     * Recomendado para mantener el contexto de persistencia JPA.
     */
    public static void copyToExistingEntity(CitaUpdateDTO dto, Cita entity) {
        if (dto == null || entity == null) return;

        entity.setFechaHora(dto.getFechaHora());
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(dto.getEstadoCita());
        entity.setPaciente(toUserReference(dto.getPacienteId()));
        entity.setParada(toParadaReference(dto.getParadaId()));
        entity.setTecnico(toUserReference(dto.getTecnicoId()));
        entity.setDoctor(toUserReference(dto.getDoctorId()));
    }

    private static Long getParadaId(Parada parada) {
        return parada != null ? parada.getId() : null;
    }

    private static Long getUserId(User user) {
        return user != null ? user.getId() : null;
    }

    private static User toUserReference(Long id) {
        if (id == null) {
            return null;
        }

        User user = new User();
        user.setId(id);
        return user;
    }

    private static Parada toParadaReference(Long id) {
        if (id == null) {
            return null;
        }
        Parada parada = new Parada();
        parada.setId(id);
        return parada;
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

    private static String getDisplayName(User user) {
        if (user == null) {
            return null;
        }

        if (user.getProfile() != null) {
            String firstName = user.getProfile().getFirstName();
            String lastName = user.getProfile().getLastName();
            String fullName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
            if (!fullName.isEmpty()) {
                return fullName;
            }
        }

        return user.getEmail();
    }
}
