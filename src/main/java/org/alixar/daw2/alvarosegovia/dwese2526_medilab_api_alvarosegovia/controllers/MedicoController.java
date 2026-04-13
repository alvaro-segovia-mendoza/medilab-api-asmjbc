package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RegistroClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RegistroClinicoReviewDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.RegistroClinicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Fachada REST para las operaciones propias del rol médico.
 */
@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Médicos", description = "Operaciones del rol médico")
public class MedicoController {

    private static final Logger logger = LoggerFactory.getLogger(MedicoController.class);

    @Autowired
    private RegistroClinicoService registroClinicoService;

    @GetMapping("/registros-clinicos/pendientes")
    @Operation(summary = "Listar registros pendientes", description = "Devuelve los registros clínicos pendientes de revisión médica.")
    public ResponseEntity<List<RegistroClinicoDTO>> getPendingClinicalRecords() {
        logger.info("Consultando registros clínicos pendientes de revisión médica");
        return ResponseEntity.ok(registroClinicoService.listPendingReview());
    }

    @PostMapping("/registros-clinicos/{id}/review")
    @Operation(summary = "Revisar registro clínico", description = "Aprueba o rechaza un registro clínico como médico.")
    public ResponseEntity<RegistroClinicoDTO> reviewClinicalRecord(@PathVariable Long id,
                                                                   @Valid @RequestBody RegistroClinicoReviewDTO dto) {
        logger.info("Revisando registro clínico {} desde la fachada médica", id);
        return ResponseEntity.ok(registroClinicoService.review(id, dto));
    }

    @GetMapping("/registros-clinicos/{id}")
    @Operation(summary = "Consultar registro clínico", description = "Devuelve el detalle de un registro clínico visible para el médico.")
    public ResponseEntity<RegistroClinicoDTO> getClinicalRecord(@PathVariable Long id) {
        logger.info("Consultando registro clínico {} desde la fachada médica", id);
        return ResponseEntity.ok(registroClinicoService.getDetail(id));
    }

    @GetMapping("/pacientes/{pacienteId}/historial")
    @Operation(summary = "Consultar historial clínico", description = "Devuelve el historial clínico agregado de un paciente.")
    public ResponseEntity<List<RegistroClinicoDTO>> getPatientHistory(@PathVariable Long pacienteId) {
        logger.info("Consultando historial clínico del paciente {}", pacienteId);
        return ResponseEntity.ok(registroClinicoService.getPatientHistory(pacienteId));
    }
}
