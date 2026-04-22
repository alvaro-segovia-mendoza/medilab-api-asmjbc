package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrailerService {
    List<TrailerDTO> list();
    Page<TrailerDTO> listPaged(Pageable pageable);
    TrailerDTO getDetail(Long id);
    TrailerDTO create(TrailerCreateDTO dto);
    TrailerDTO update(TrailerUpdateDTO dto);
    void delete(Long id);
}
