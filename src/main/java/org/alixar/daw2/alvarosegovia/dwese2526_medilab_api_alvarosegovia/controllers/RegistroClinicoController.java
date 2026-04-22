package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.RegistroClinicoReviewDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical.RegistroClinicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/registros-clinicos")
@Tag(name = "Registros clínicos", description = "Gestión de registros clínicos y revisión médica")
public class RegistroClinicoController {

    private static final Logger logger = LoggerFactory.getLogger(RegistroClinicoController.class);

    @Autowired
    private RegistroClinicoService registroClinicoService;

    @PostMapping
    @Operation(summary = "Crear registro clínico", description = "Crea un registro clínico en borrador para una cita.")
    public ResponseEntity<RegistroClinicoDTO> create(@Valid @RequestBody RegistroClinicoCreateDTO dto) {
        logger.info("Creando registro clínico para cita {}", dto.getCitaId());
        RegistroClinicoDTO created = registroClinicoService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/{id}/submit-review")
    @Operation(summary = "Enviar a revisión", description = "Envía un borrador técnico a revisión médica.")
    public ResponseEntity<RegistroClinicoDTO> submitForReview(@PathVariable Long id) {
        logger.info("Enviando registro clínico {} a revisión médica", id);
        return ResponseEntity.ok(registroClinicoService.submitForReview(id));
    }

    @PostMapping("/{id}/review")
    @Operation(summary = "Revisar registro clínico", description = "Aprueba o rechaza un registro clínico pendiente de revisión.")
    public ResponseEntity<RegistroClinicoDTO> review(@PathVariable Long id,
                                                     @Valid @RequestBody RegistroClinicoReviewDTO dto) {
        logger.info("Revisando registro clínico {}", id);
        return ResponseEntity.ok(registroClinicoService.review(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de registro clínico", description = "Devuelve el detalle de un registro clínico.")
    public ResponseEntity<RegistroClinicoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(registroClinicoService.getDetail(id));
    }

    @GetMapping("/cita/{citaId}")
    @Operation(summary = "Listar registros por cita", description = "Devuelve los registros clínicos asociados a una cita.")
    public ResponseEntity<List<RegistroClinicoDTO>> getByCita(@PathVariable Long citaId) {
        return ResponseEntity.ok(registroClinicoService.listByCita(citaId));
    }

    @GetMapping("/paciente/{pacienteId}/historial")
    @Operation(summary = "Historial por paciente", description = "Devuelve los registros clínicos confirmados de un paciente.")
    public ResponseEntity<List<RegistroClinicoDTO>> getHistory(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(registroClinicoService.getPatientHistory(pacienteId));
    }
}
