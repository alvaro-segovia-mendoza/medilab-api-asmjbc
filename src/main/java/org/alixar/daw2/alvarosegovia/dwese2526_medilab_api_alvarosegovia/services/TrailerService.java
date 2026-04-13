package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerUpdateDTO;

import java.util.List;

public interface TrailerService {
    List<TrailerDTO> list();
    TrailerDTO getDetail(Long id);
    TrailerDTO create(TrailerCreateDTO dto);
    TrailerDTO update(TrailerUpdateDTO dto);
    void delete(Long id);
}
