package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CitaService {

    Page<CitaDTO> list(Pageable pageable);

    Page<CitaDTO> listAdmin(Cita.EstadoCita estado, Long pacienteId, Pageable pageable);

    CitaUpdateDTO getForEdit(Long id);

    CitaDTO create(CitaCreateDTO dto);

    CitaDTO update(CitaUpdateDTO dto);

    CitaDTO confirm(Long id);

    CitaDTO cancel(Long id);

    void delete(Long id);

    CitaDetailDTO getDetail(Long id);

    List<CitaDTO> getAllCitas();
}
