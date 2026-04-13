package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Ruta;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Trailer;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.RutaMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.TrailerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RutaServiceImpl implements RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private TrailerRepository trailerRepository;

    @Override
    public List<RutaDTO> list() {
        return RutaMapper.toDTOList(rutaRepository.findAll());
    }

    @Override
    public List<RutaDTO> listActive() {
        return RutaMapper.toDTOList(rutaRepository.findByActivaTrueOrderByNombreAsc());
    }

    @Override
    public RutaDTO getDetail(Long id) {
        return RutaMapper.toDTO(findRuta(id));
    }

    @Override
    public RutaDTO create(RutaCreateDTO dto) {
        Ruta ruta = Ruta.builder()
                .nombre(dto.getNombre())
                .origen(dto.getOrigen())
                .destino(dto.getDestino())
                .activa(dto.getActiva() == null || dto.getActiva())
                .trailer(findTrailer(dto.getTrailerId()))
                .build();
        return RutaMapper.toDTO(rutaRepository.save(ruta));
    }

    @Override
    public RutaDTO update(RutaUpdateDTO dto) {
        Ruta ruta = findRuta(dto.getId());
        ruta.setNombre(dto.getNombre());
        ruta.setOrigen(dto.getOrigen());
        ruta.setDestino(dto.getDestino());
        ruta.setActiva(dto.getActiva());
        ruta.setTrailer(findTrailer(dto.getTrailerId()));
        return RutaMapper.toDTO(rutaRepository.save(ruta));
    }

    @Override
    public void delete(Long id) {
        if (!rutaRepository.existsById(id)) {
            throw new ResourceNotFoundException("ruta", "id", id);
        }
        rutaRepository.deleteById(id);
    }

    private Ruta findRuta(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ruta", "id", id));
    }

    private Trailer findTrailer(Long id) {
        return trailerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("trailer", "id", id));
    }
}
