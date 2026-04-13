package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaUpdateDTO;

import java.util.List;

public interface RutaService {
    List<RutaDTO> list();
    List<RutaDTO> listActive();
    RutaDTO getDetail(Long id);
    RutaDTO create(RutaCreateDTO dto);
    RutaDTO update(RutaUpdateDTO dto);
    void delete(Long id);
}
