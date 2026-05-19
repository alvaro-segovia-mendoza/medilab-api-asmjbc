package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrato de aplicacion para la gestion clinica y administrativa de citas.
 */
public interface CitaService {

    /**
     * Lista citas visibles en el contexto general.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de citas.
     */
    Page<CitaDTO> list(Pageable pageable);

    /**
     * Lista citas para el panel administrativo con filtros opcionales.
     *
     * @param estado estado de cita a filtrar.
     * @param pacienteId identificador del paciente.
     * @param pageable configuracion de pagina y orden.
     * @return pagina de citas filtradas.
     */
    Page<CitaDTO> listAdmin(Cita.EstadoCita estado, Long pacienteId, Pageable pageable);

    /**
     * Obtiene los datos de una cita para editarla.
     *
     * @param id identificador de la cita.
     * @return DTO preparado para edicion.
     */
    CitaUpdateDTO getForEdit(Long id);

    /**
     * Crea una nueva cita.
     *
     * @param dto datos de alta de la cita.
     * @return cita creada.
     */
    CitaDTO create(CitaCreateDTO dto);

    /**
     * Actualiza una cita existente.
     *
     * @param dto datos actualizados de la cita.
     * @return cita actualizada.
     */
    CitaDTO update(CitaUpdateDTO dto);

    /**
     * Confirma una cita pendiente.
     *
     * @param id identificador de la cita.
     * @return cita confirmada.
     */
    CitaDTO confirm(Long id);

    /**
     * Cancela una cita existente.
     *
     * @param id identificador de la cita.
     * @return cita cancelada.
     */
    CitaDTO cancel(Long id);

    /**
     * Elimina una cita.
     *
     * @param id identificador de la cita.
     */
    void delete(Long id);

    /**
     * Recupera el detalle de una cita.
     *
     * @param id identificador de la cita.
     * @return detalle de la cita.
     */
    CitaDetailDTO getDetail(Long id);

    /**
     * Lista todas las citas sin paginacion.
     *
     * @return lista completa de citas.
     */
    List<CitaDTO> getAllCitas();
}
