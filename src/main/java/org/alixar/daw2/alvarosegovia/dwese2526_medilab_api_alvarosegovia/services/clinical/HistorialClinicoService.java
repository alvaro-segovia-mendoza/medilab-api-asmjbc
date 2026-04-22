package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.HistorialClinicoDTO;

public interface HistorialClinicoService {

    HistorialClinicoDTO getPatientHistory(Long pacienteId);
}
