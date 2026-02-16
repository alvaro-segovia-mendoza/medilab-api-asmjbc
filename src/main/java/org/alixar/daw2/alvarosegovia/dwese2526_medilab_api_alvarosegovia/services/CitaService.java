package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CitaService {

    Page<CitaDTO> list(Pageable pageable);

    CitaUpdateDTO getForEdit(Long id);

    CitaDTO create(CitaCreateDTO dto);

    CitaDTO update(CitaUpdateDTO dto);

    void delete(Long id);

    CitaDetailDTO getDetail(Long id);

    List<CitaDTO> getAllCitas();
}
