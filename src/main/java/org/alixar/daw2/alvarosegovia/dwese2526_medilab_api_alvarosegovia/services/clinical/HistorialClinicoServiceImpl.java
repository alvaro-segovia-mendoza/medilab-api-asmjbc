package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.HistorialClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserBasicDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    @Autowired
    private RegistroClinicoService registroClinicoService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public HistorialClinicoDTO getPatientHistory(Long pacienteId) {
        User paciente = userRepository.findByIdWithProfile(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("paciente", "id", pacienteId));

        List<RegistroClinicoDTO> registros = registroClinicoService.getPatientHistory(pacienteId);

        return HistorialClinicoDTO.builder()
                .pacienteId(pacienteId)
                .paciente(toUserBasicDTO(paciente))
                .totalRegistros(registros.size())
                .ultimaActualizacion(resolveLastUpdate(registros))
                .registros(registros)
                .build();
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
