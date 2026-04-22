package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RutaService {
    List<RutaDTO> list();
    Page<RutaDTO> listPaged(Pageable pageable);
    List<RutaDTO> listActive();
    RutaDTO getDetail(Long id);
    RutaDTO create(RutaCreateDTO dto);
    RutaDTO update(RutaUpdateDTO dto);
    void delete(Long id);
}
