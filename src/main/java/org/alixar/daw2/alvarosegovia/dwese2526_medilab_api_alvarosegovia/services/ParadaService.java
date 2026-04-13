package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Parada;

import java.time.LocalDate;
import java.util.List;

public interface ParadaService {
    List<ParadaDTO> list();
    ParadaDTO getDetail(Long id);
    ParadaDTO create(ParadaCreateDTO dto);
    ParadaDTO update(ParadaUpdateDTO dto);
    void delete(Long id);
    List<ParadaDTO> listByRuta(Long rutaId);
    List<ParadaDTO> listActiveByFecha(LocalDate fecha);
    List<ParadaDTO> listActiveFromDate(LocalDate fecha);
    DisponibilidadParadaDTO getDisponibilidad(Long paradaId);
    Parada getEntity(Long id);
}
