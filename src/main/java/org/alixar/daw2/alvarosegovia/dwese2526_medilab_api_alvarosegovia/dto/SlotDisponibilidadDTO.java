package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Disponibilidad agregada de una franja horaria concreta dentro de una parada.")
public class SlotDisponibilidadDTO {
    @Schema(description = "Identificadores de slots reservables en esta franja.", example = "[5,6]")
    private List<Long> slotIdsDisponibles;
    @Schema(description = "Inicio de la franja agregada.", example = "2026-04-21T10:00:00")
    private LocalDateTime fechaHora;
    private Integer reservasActivas;
    private Integer plazasDisponibles;
    private boolean reservable;
}
