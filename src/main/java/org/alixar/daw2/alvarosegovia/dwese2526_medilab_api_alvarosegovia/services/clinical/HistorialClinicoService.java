package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.HistorialClinicoDTO;

/**
 * Contrato de aplicacion para obtener historiales clinicos agregados por paciente.
 */
public interface HistorialClinicoService {

    /**
     * Obtiene el historial clinico agregado de un paciente.
     *
     * @param pacienteId identificador del paciente.
     * @return historial clinico compuesto.
     */
    HistorialClinicoDTO getPatientHistory(Long pacienteId);
}
