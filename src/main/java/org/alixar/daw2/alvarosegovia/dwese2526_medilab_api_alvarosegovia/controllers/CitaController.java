package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.common.ApiErrorDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.clinical.CitaUpdateDTO;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Cita;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.clinical.CitaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST que maneja las operaciones CRUD para la entidad 'Cita'.
 */
@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Reserva y gestión operativa de citas")
public class CitaController {

    private static final Logger logger = LoggerFactory.getLogger(CitaController.class);

    @Autowired
    private CitaService citaService;

    /**
     * Lista paginada de citas en JSON.
     * Para administradores, acepta filtros opcionales por estado y pacienteId.
     */
    @GetMapping
    @Operation(summary = "Listar citas", description = "Devuelve las citas visibles para el usuario autenticado en formato paginado. El admin puede filtrar por estado y pacienteId. La ordenación por defecto se realiza por la fecha y hora de inicio del slot reservado.")
    public ResponseEntity<Page<CitaDTO>> getCitas(
            @RequestParam(required = false) Cita.EstadoCita estado,
            @RequestParam(required = false) Long pacienteId,
            @PageableDefault(size = 10, sort = "slot.fechaHoraInicio", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Listando citas (REST) page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<CitaDTO> page = isAdmin() && (estado != null || pacienteId != null)
                ? citaService.listAdmin(estado, pacienteId, pageable)
                : citaService.list(pageable);

        logger.info("Se han cargado {} citas en la página {}.", page.getNumberOfElements(), page.getNumber());

        return ResponseEntity.ok(page);
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @GetMapping("/all")
    @Operation(summary = "Exportar todas las citas (sin paginación)",
            description = "Devuelve todas las citas visibles para el usuario autenticado sin paginar. Uso recomendado: exportación o reporting. Para listado normal usar GET /api/citas (paginado).")
    public ResponseEntity<List<CitaDTO>> getAllCitas() {
        logger.info("Solicitando la lista de todas las citas...");
        List<CitaDTO> citas = citaService.getAllCitas();
        return ResponseEntity.ok(citas);
    }
    /**
     * Devuelve el detalle de una cita por ID en JSON.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Detalle de cita", description = "Devuelve una cita con paciente, técnico, médico, slot reservado, parada, ruta y trailer.")
    public ResponseEntity<CitaDetailDTO> getCitaById(@PathVariable Long id) {

        logger.info("Retornando cita de id {}", id);

        CitaDetailDTO citaDTO = citaService.getDetail(id);

        return ResponseEntity.ok(citaDTO);
    }

    /**
     * Crea una nueva cita.
     */
    @PostMapping
    @Operation(summary = "Reservar cita", description = "Reserva una cita sobre un slot concreto (`slotId`) y asigna automáticamente un técnico disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cita creada"),
            @ApiResponse(responseCode = "400", description = "Slot no disponible o inconsistencia operativa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    public ResponseEntity<CitaDTO> createCita(@Valid @RequestBody CitaCreateDTO dto) {

        logger.info("Creando cita {}", dto);

        CitaDTO created = citaService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * Actualiza una cita existente.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita", description = "Actualiza una cita existente y permite moverla a otro slot disponible.")
    public ResponseEntity<CitaDTO> updateCita(@PathVariable Long id,
                                                      @Valid @RequestBody CitaUpdateDTO dto) {

        logger.info("Actualizando cita con ID {}", id);

        dto.setId(id);

        CitaDTO updated = citaService.update(dto);

        logger.info("Cita con ID {} actualizada con éxito.", id);

        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmar cita", description = "Confirma una cita pendiente. Solo técnico asignado o admin.")
    public ResponseEntity<CitaDTO> confirmCita(@PathVariable Long id) {
        logger.info("Confirmando cita con ID {}", id);
        return ResponseEntity.ok(citaService.confirm(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar cita", description = "Cancela una cita pendiente o confirmada. Solo técnico asignado o admin.")
    public ResponseEntity<CitaDTO> cancelCita(@PathVariable Long id) {
        logger.info("Cancelando cita con ID {}", id);
        return ResponseEntity.ok(citaService.cancel(id));
    }

    /**
     * Elimina una cita por ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cita", description = "Elimina una cita por id. Solo admin.")
    public ResponseEntity<Void> deleteCita(@PathVariable Long id) {

        logger.info("Eliminando cita con id: {}", id);

        citaService.delete(id);

        logger.info("Cita con ID {} eliminada con éxito.", id);

        return ResponseEntity.noContent().build();
    }
}
