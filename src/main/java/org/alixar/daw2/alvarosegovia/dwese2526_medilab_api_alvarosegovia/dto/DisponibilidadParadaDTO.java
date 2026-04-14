package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadParadaDTO {
    private Long paradaId;
    private String paradaNombre;
    private String municipio;
    private String provincia;
    private Integer capacidadMaxima;
    private List<SlotDisponibilidadDTO> slots;
    private List<java.time.LocalDateTime> slotsDisponibles;
}
