package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.HistorialClinicoDTO;

public interface HistorialClinicoService {

    HistorialClinicoDTO getPatientHistory(Long pacienteId);
}
