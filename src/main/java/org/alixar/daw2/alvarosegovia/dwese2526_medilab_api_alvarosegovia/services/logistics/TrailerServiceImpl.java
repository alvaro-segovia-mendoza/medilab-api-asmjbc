package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.TrailerMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TrailerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacion del servicio de gestion de trailers.
 */
@Service
@Transactional
public class TrailerServiceImpl implements TrailerService {

    private static final Logger logger = LoggerFactory.getLogger(TrailerServiceImpl.class);

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
        logger.info("Creando trailer con codigo={}", dto.getCodigo());
        if (trailerRepository.existsByCodigo(dto.getCodigo())) {
            logger.warn("Intento de alta duplicada para trailer codigo={}", dto.getCodigo());
            throw new DuplicateResourceException("trailer", "codigo", dto.getCodigo());
        }
        TrailerDTO created = TrailerMapper.toDTO(trailerRepository.save(TrailerMapper.toEntity(dto)));
        logger.info("Trailer creado con id={}", created.getId());
        return created;
    }

    @Override
    public TrailerDTO update(TrailerUpdateDTO dto) {
        logger.info("Actualizando trailer id={}", dto.getId());
        if (trailerRepository.existsByCodigoAndIdNot(dto.getCodigo(), dto.getId())) {
            logger.warn("Intento de actualizacion duplicada para trailer id={}", dto.getId());
            throw new DuplicateResourceException("trailer", "codigo", dto.getCodigo());
        }
        Trailer trailer = findTrailer(dto.getId());
        TrailerMapper.copyToExistingEntity(dto, trailer);
        TrailerDTO updated = TrailerMapper.toDTO(trailerRepository.save(trailer));
        logger.info("Trailer actualizado con id={}", updated.getId());
        return updated;
    }

    @Override
    public void delete(Long id) {
        logger.info("Eliminando trailer id={}", id);
        if (!trailerRepository.existsById(id)) {
            throw new ResourceNotFoundException("trailer", "id", id);
        }
        trailerRepository.deleteById(id);
        logger.info("Trailer eliminado con id={}", id);
    }

    private Trailer findTrailer(Long id) {
        return trailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("trailer", "id", id));
    }
}
