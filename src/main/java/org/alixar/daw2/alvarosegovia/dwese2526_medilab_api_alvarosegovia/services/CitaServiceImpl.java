package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.CitaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.CitaMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    /**
     * Obtiene una lista paginada de citas.
     */
    @Override
    public Page<CitaDTO> list(Pageable pageable) {
        return citaRepository.findAll(pageable)
                .map(CitaMapper::toDTO);
    }

    /**
     * Obtiene los datos de una cita para su edición.
     */
    @Override
    public CitaUpdateDTO getForEdit(Long id) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("cita", "id", id)
                );

        return CitaMapper.toUpdateDTO(cita);
    }

    /**
     * Crea una nueva cita.
     */
    @Override
    public CitaDTO create(CitaCreateDTO dto) {

        if (citaRepository.existsByCodigo(dto.getCodigo())) {
            throw new DuplicateResourceException("cita", "codigo", dto.getCodigo());
        }

        Cita cita = CitaMapper.toEntity(dto);
        cita = citaRepository.save(cita);
        return CitaMapper.toDTO(cita);
    }

    /**
     * Actualiza una cita existente.
     */
    @Override
    public CitaDTO update(CitaUpdateDTO dto) {

        if (citaRepository.existsByCodigoAndIdNot(dto.getCodigo(), dto.getId())) {
            throw new ResourceNotFoundException("cita", "codigo", dto.getCodigo());
        }

        Cita cita = citaRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("cita", "id", dto.getId())
                );

        CitaMapper.copyToExistingEntity(dto, cita);
        cita = citaRepository.save(cita);
        return CitaMapper.toDTO(cita);
    }

    /**
     * Elimina una cita por su identificador.
     */
    @Override
    public void delete(Long id) {

        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("cita", "id", id);
        }

        citaRepository.deleteById(id);
    }

    /**
     * Obtiene el detalle completo de una cita.
     */
    @Override
    public CitaDetailDTO getDetail(Long id) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("cita", "id", id)
                );

        return CitaMapper.toDetailDTO(cita);
    }

    @Override
    public List<CitaDTO> getAllCitas() {
        List<Cita> regions = citaRepository.findAll();

        // Convertimos cada entidad a DTO usando el mapper
        return regions.stream()
                .map(CitaMapper::toDTO)
                .toList();
    }
}
