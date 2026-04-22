package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoReviewDTO;

import java.util.List;

public interface RegistroClinicoService {

    RegistroClinicoDTO create(RegistroClinicoCreateDTO dto);

    RegistroClinicoDTO submitForReview(Long id);

    RegistroClinicoDTO review(Long id, RegistroClinicoReviewDTO dto);

    RegistroClinicoDTO getDetail(Long id);

    List<RegistroClinicoDTO> listByCita(Long citaId);

    List<RegistroClinicoDTO> listPendingReview();

    List<RegistroClinicoDTO> getPatientHistory(Long pacienteId);
}
