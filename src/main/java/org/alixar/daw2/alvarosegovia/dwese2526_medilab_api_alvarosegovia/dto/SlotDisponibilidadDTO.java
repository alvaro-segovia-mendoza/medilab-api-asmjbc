package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotDisponibilidadDTO {
    private LocalDateTime fechaHora;
    private Integer reservasActivas;
    private Integer plazasDisponibles;
    private boolean reservable;
}
