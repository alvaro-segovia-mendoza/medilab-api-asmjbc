package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.TrailerMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TrailerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrailerServiceImpl implements TrailerService {

    @Autowired
    private TrailerRepository trailerRepository;

    @Override
    public List<TrailerDTO> list() {
        return TrailerMapper.toDTOList(trailerRepository.findAll());
    }

    @Override
    public Page<TrailerDTO> listPaged(Pageable pageable) {
        return trailerRepository.findAll(pageable).map(TrailerMapper::toDTO);
    }

    @Override
    public TrailerDTO getDetail(Long id) {
        return TrailerMapper.toDTO(findTrailer(id));
    }

    @Override
    public TrailerDTO create(TrailerCreateDTO dto) {
        if (trailerRepository.existsByCodigo(dto.getCodigo())) {
            throw new DuplicateResourceException("trailer", "codigo", dto.getCodigo());
        }
        return TrailerMapper.toDTO(trailerRepository.save(TrailerMapper.toEntity(dto)));
    }

    @Override
    public TrailerDTO update(TrailerUpdateDTO dto) {
        if (trailerRepository.existsByCodigoAndIdNot(dto.getCodigo(), dto.getId())) {
            throw new DuplicateResourceException("trailer", "codigo", dto.getCodigo());
        }
        Trailer trailer = findTrailer(dto.getId());
        TrailerMapper.copyToExistingEntity(dto, trailer);
        return TrailerMapper.toDTO(trailerRepository.save(trailer));
    }

    @Override
    public void delete(Long id) {
        if (!trailerRepository.existsById(id)) {
            throw new ResourceNotFoundException("trailer", "id", id);
        }
        trailerRepository.deleteById(id);
    }

    private Trailer findTrailer(Long id) {
        return trailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("trailer", "id", id));
    }
}
