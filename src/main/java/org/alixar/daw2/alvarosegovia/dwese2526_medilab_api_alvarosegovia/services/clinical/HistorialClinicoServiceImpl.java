package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.HistorialClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementacion del servicio que agrega registros clinicos confirmados en un historial por paciente.
 */
@Service
@Transactional(readOnly = true)
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private static final Logger logger = LoggerFactory.getLogger(HistorialClinicoServiceImpl.class);

    @Autowired
    private RegistroClinicoService registroClinicoService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public HistorialClinicoDTO getPatientHistory(Long pacienteId) {
        logger.info("Construyendo historial clinico agregado para pacienteId={}", pacienteId);
        User paciente = userRepository.findByIdWithProfile(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("paciente", "id", pacienteId));

        List<RegistroClinicoDTO> registros = registroClinicoService.getPatientHistory(pacienteId);

        HistorialClinicoDTO historial = HistorialClinicoDTO.builder()
                .pacienteId(pacienteId)
                .paciente(toUserBasicDTO(paciente))
                .totalRegistros(registros.size())
                .ultimaActualizacion(resolveLastUpdate(registros))
                .registros(registros)
                .build();
        logger.info("Historial clinico agregado generado para pacienteId={} con totalRegistros={}", pacienteId, historial.getTotalRegistros());
        return historial;
    }

    private LocalDateTime resolveLastUpdate(List<RegistroClinicoDTO> registros) {
        return registros.stream()
                .map(registro -> registro.getConfirmedAt() != null ? registro.getConfirmedAt() : registro.getUpdatedAt())
                .filter(value -> value != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private UserBasicDTO toUserBasicDTO(User user) {
        UserBasicDTO dto = new UserBasicDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        if (user.getProfile() != null) {
            dto.setFirstName(user.getProfile().getFirstName());
            dto.setLastName(user.getProfile().getLastName());
        }
        return dto;
    }
}
