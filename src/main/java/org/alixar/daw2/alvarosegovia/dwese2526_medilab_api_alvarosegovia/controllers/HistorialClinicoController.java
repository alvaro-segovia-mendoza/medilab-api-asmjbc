package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.HistorialClinicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.HistorialClinicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historiales-clinicos")
@Tag(name = "Historial clínico", description = "Consulta agregada del historial médico del paciente")
public class HistorialClinicoController {

    private static final Logger logger = LoggerFactory.getLogger(HistorialClinicoController.class);

    @Autowired
    private HistorialClinicoService historialClinicoService;

    @GetMapping("/pacientes/{pacienteId}")
    @Operation(summary = "Consultar historial clínico agregado", description = "Devuelve el historial clínico agregado de un paciente con sus registros confirmados.")
    public ResponseEntity<HistorialClinicoDTO> getByPaciente(@PathVariable Long pacienteId) {
        logger.info("Consultando historial clínico agregado del paciente {}", pacienteId);
        return ResponseEntity.ok(historialClinicoService.getPatientHistory(pacienteId));
    }
}
