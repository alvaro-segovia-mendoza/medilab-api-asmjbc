package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.SlotCita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;

import java.time.LocalDateTime;
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

        Parada parada = getParada(entity);
        CitaDTO dto = new CitaDTO();
        dto.setId(entity.getId());
        dto.setSlotId(getSlotId(entity.getSlot()));
        dto.setFechaHora(getFechaHora(entity));
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());
        dto.setPacienteNombre(getDisplayName(entity.getPaciente()));
        dto.setTecnicoNombre(getDisplayName(entity.getTecnico()));
        dto.setDoctorNombre(getDisplayName(entity.getDoctor()));
        dto.setParadaNombre(parada != null ? parada.getNombre() : null);
        dto.setMunicipioParada(parada != null ? parada.getMunicipio() : null);
        dto.setProvinciaParada(parada != null ? parada.getProvincia() : null);
        dto.setRutaNombre(parada != null && parada.getRuta() != null ? parada.getRuta().getNombre() : null);
        dto.setTrailerNombre(parada != null && parada.getRuta() != null && parada.getRuta().getTrailer() != null ? parada.getRuta().getTrailer().getNombre() : null);

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

        Parada parada = getParada(entity);
        CitaDetailDTO dto = new CitaDetailDTO();
        dto.setId(entity.getId());
        dto.setSlotId(getSlotId(entity.getSlot()));
        dto.setFechaHora(getFechaHora(entity));
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());
        dto.setPaciente(toUserBasicDTO(entity.getPaciente()));
        dto.setTecnico(toUserBasicDTO(entity.getTecnico()));
        dto.setDoctor(toUserBasicDTO(entity.getDoctor()));
        dto.setParada(ParadaMapper.toBasicDTO(parada));
        dto.setRutaNombre(parada != null && parada.getRuta() != null ? parada.getRuta().getNombre() : null);
        dto.setTrailerNombre(parada != null && parada.getRuta() != null && parada.getRuta().getTrailer() != null ? parada.getRuta().getTrailer().getNombre() : null);

        return dto;
    }

    // -----------------------------------------
    // Entity -> DTO (edición)
    // -----------------------------------------

    public static CitaUpdateDTO toUpdateDTO(Cita entity) {
        if (entity == null) return null;

        CitaUpdateDTO dto = new CitaUpdateDTO();
        dto.setId(entity.getId());
        dto.setTipoPrueba(entity.getTipoPrueba());
        dto.setEstadoCita(entity.getEstado());
        dto.setPacienteId(getUserId(entity.getPaciente()));
        dto.setSlotId(getSlotId(entity.getSlot()));
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
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(Cita.EstadoCita.PENDIENTE);
        entity.setPaciente(toUserReference(dto.getPacienteId()));
        entity.setSlot(toSlotReference(dto.getSlotId()));

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
        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(dto.getEstadoCita());
        entity.setPaciente(toUserReference(dto.getPacienteId()));
        entity.setSlot(toSlotReference(dto.getSlotId()));
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

        entity.setTipoPrueba(dto.getTipoPrueba());
        entity.setEstado(dto.getEstadoCita());
        entity.setPaciente(toUserReference(dto.getPacienteId()));
        entity.setSlot(toSlotReference(dto.getSlotId()));
        entity.setTecnico(toUserReference(dto.getTecnicoId()));
        entity.setDoctor(toUserReference(dto.getDoctorId()));
    }

    private static Long getSlotId(SlotCita slot) {
        return slot != null ? slot.getId() : null;
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

    private static SlotCita toSlotReference(Long id) {
        if (id == null) {
            return null;
        }
        SlotCita slot = new SlotCita();
        slot.setId(id);
        return slot;
    }

    private static LocalDateTime getFechaHora(Cita cita) {
        return cita != null && cita.getSlot() != null ? cita.getSlot().getFechaHoraInicio() : null;
    }

    private static Parada getParada(Cita cita) {
        return cita != null && cita.getSlot() != null ? cita.getSlot().getParada() : null;
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
