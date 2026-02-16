package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Tecnico;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.TecnicoMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TecnicoServiceImpl implements TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Override
    public Page<TecnicoDTO> list(Pageable pageable) {
        return tecnicoRepository.findAll(pageable)
                .map(TecnicoMapper::toDTO);
    }

    @Override
    public TecnicoUpdateDTO getForEdit(Long id) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("tecnico", "id", id)
                );
        return TecnicoMapper.toUpdateDTO(tecnico);
    }

    @Override
    public TecnicoDTO create(TecnicoCreateDTO dto) {

        if (tecnicoRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceNotFoundException("email", "email", dto.getEmail());
        }

        Tecnico tecnico = TecnicoMapper.toEntity(dto);
        tecnico = tecnicoRepository.save(tecnico);
        return TecnicoMapper.toDTO(tecnico);
    }

    @Override
    public TecnicoDTO update(TecnicoUpdateDTO dto) {

        if (tecnicoRepository.existsByEmailAndIdNot(dto.getEmail() ,dto.getId())) {
            throw new ResourceNotFoundException("tecnico", "email", dto.getEmail());
        }

        Tecnico tecnico = tecnicoRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("tecnico", "id", dto.getId())
                );

        TecnicoMapper.copyToExistingEntity(dto, tecnico);
        tecnico = tecnicoRepository.save(tecnico);
        return TecnicoMapper.toDTO(tecnico);
    }

    @Override
    public void delete(Long id) {
        if (!tecnicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("tecnico", "id", id);
        }
        tecnicoRepository.deleteById(id);
    }

    @Override
    public TecnicoDetailDTO getDetail(Long id) {

        Tecnico tecnico = tecnicoRepository.findByIdWithCitas(id)
                .orElseThrow(() -> new ResourceNotFoundException("tecnico", "id", id)
                );
        return TecnicoMapper.toDetailDTO(tecnico);
    }

    @Override
    public List<TecnicoDTO> getAllTecnicos() {
        // Recuperamos todas las tecnicoes desde la base de datos
        List<Tecnico> tecnicos = tecnicoRepository.findAll();

        // Convertimos cada entidad a DTO usando el mapper
        return tecnicos.stream()
                .map(TecnicoMapper::toDTO)
                .toList();
    }
}
