package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical.CitaService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical.RegistroClinicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Fachada REST para las operaciones propias del rol técnico.
 * No modela una entidad "Tecnico" independiente; agrupa acciones sobre citas y registros clínicos.
 */
@RestController
@RequestMapping("/api/tecnicos")
@Tag(name = "Técnicos", description = "Operaciones del rol técnico")
public class TecnicoController {

    private static final Logger logger = LoggerFactory.getLogger(TecnicoController.class);

    @Autowired
    private CitaService citaService;

    @Autowired
    private RegistroClinicoService registroClinicoService;

    @GetMapping("/citas/{citaId}")
    @Operation(summary = "Consultar cita asignada", description = "Devuelve el detalle de una cita asignada al técnico.")
    public ResponseEntity<CitaDetailDTO> getAssignedCita(@PathVariable Long citaId) {
        logger.info("Consultando detalle de cita {} desde la fachada de técnico", citaId);
        return ResponseEntity.ok(citaService.getDetail(citaId));
    }

    @GetMapping("/citas/{citaId}/registros")
    @Operation(summary = "Consultar registros de una cita", description = "Devuelve los registros clínicos de una cita asignada.")
    public ResponseEntity<List<RegistroClinicoDTO>> getClinicalRecordsByCita(@PathVariable Long citaId) {
        logger.info("Consultando registros clínicos de la cita {}", citaId);
        return ResponseEntity.ok(registroClinicoService.listByCita(citaId));
    }

    @PostMapping("/registros-clinicos")
    @Operation(summary = "Crear registro clínico técnico", description = "Crea un registro clínico en borrador como técnico asignado.")
    public ResponseEntity<RegistroClinicoDTO> createClinicalRecord(@Valid @RequestBody RegistroClinicoCreateDTO dto) {
        logger.info("Creando registro clínico técnico para la cita {}", dto.getCitaId());
        RegistroClinicoDTO created = registroClinicoService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/registros-clinicos/{id}/submit-review")
    @Operation(summary = "Enviar registro a revisión", description = "Envía un registro técnico a revisión médica.")
    public ResponseEntity<RegistroClinicoDTO> submitClinicalRecordForReview(@PathVariable Long id) {
        logger.info("Enviando registro clínico {} a revisión médica desde la fachada de técnico", id);
        return ResponseEntity.ok(registroClinicoService.submitForReview(id));
    }

    @GetMapping("/registros-clinicos/{id}")
    @Operation(summary = "Consultar registro clínico", description = "Devuelve un registro clínico visible para el técnico.")
    public ResponseEntity<RegistroClinicoDTO> getClinicalRecord(@PathVariable Long id) {
        logger.info("Consultando registro clínico {}", id);
        return ResponseEntity.ok(registroClinicoService.getDetail(id));
    }
}
