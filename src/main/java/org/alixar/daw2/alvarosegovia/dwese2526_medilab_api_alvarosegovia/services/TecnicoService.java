package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TecnicoService {

    Page<TecnicoDTO> list(Pageable pageable);

    TecnicoUpdateDTO getForEdit(Long id);

    TecnicoDTO create(TecnicoCreateDTO dto);

    TecnicoDTO update(TecnicoUpdateDTO dto);

    void delete(Long id);

    TecnicoDetailDTO getDetail(Long id);

    List<TecnicoDTO> getAllTecnicos();
}
