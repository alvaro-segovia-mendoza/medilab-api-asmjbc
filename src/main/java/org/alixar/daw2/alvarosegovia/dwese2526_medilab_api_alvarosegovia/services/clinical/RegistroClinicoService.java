package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoReviewDTO;

import java.util.List;

/**
 * Contrato de aplicacion para crear, revisar y consultar registros clinicos.
 */
public interface RegistroClinicoService {

    /**
     * Crea un registro clinico en estado borrador.
     *
     * @param dto datos iniciales del registro.
     * @return registro creado.
     */
    RegistroClinicoDTO create(RegistroClinicoCreateDTO dto);

    /**
     * Envia un registro borrador a revision medica.
     *
     * @param id identificador del registro.
     * @return registro actualizado.
     */
    RegistroClinicoDTO submitForReview(Long id);

    /**
     * Revisa un registro clinico pendiente.
     *
     * @param id identificador del registro.
     * @param dto datos de aprobacion o rechazo.
     * @return registro revisado.
     */
    RegistroClinicoDTO review(Long id, RegistroClinicoReviewDTO dto);

    /**
     * Recupera el detalle de un registro clinico.
     *
     * @param id identificador del registro.
     * @return detalle del registro.
     */
    RegistroClinicoDTO getDetail(Long id);

    /**
     * Lista los registros asociados a una cita.
     *
     * @param citaId identificador de la cita.
     * @return registros de la cita.
     */
    List<RegistroClinicoDTO> listByCita(Long citaId);

    /**
     * Lista los registros pendientes de revision.
     *
     * @return registros pendientes.
     */
    List<RegistroClinicoDTO> listPendingReview();

    /**
     * Recupera el historial confirmado de un paciente.
     *
     * @param pacienteId identificador del paciente.
     * @return registros confirmados del paciente.
     */
    List<RegistroClinicoDTO> getPatientHistory(Long pacienteId);
}
