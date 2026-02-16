package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.*;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.CitaService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.TecnicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * Controlador REST que maneja las operaciones CRUD para la entidad 'Cita'.
 */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private static final Logger logger = LoggerFactory.getLogger(CitaController.class);

    @Autowired
    private CitaService citaService;

    /**
     * Lista paginada de citas en JSON.
     */
    @GetMapping
    public ResponseEntity<Page<CitaDTO>> getCitas(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Listando citas (REST) page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<CitaDTO> page = citaService.list(pageable);

        logger.info("Se han cargado {} citas en la página {}.", page.getNumberOfElements(), page.getNumber());

        return ResponseEntity.ok(page);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CitaDTO>> getAllCitas() {
        logger.info("Solicitando la lista de todas las citas...");
        List<CitaDTO> citas = citaService.getAllCitas();
        return ResponseEntity.ok(citas);
    }
    /**
     * Devuelve el detalle de una cita por ID en JSON.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CitaDetailDTO> getCitaById(@PathVariable Long id) {

        logger.info("Retornando cita de id {}", id);

        CitaDetailDTO citaDTO = citaService.getDetail(id);

        return ResponseEntity.ok(citaDTO);
    }

    /**
     * Crea una nueva cita.
     */
    @PostMapping
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
    public ResponseEntity<CitaDTO> updateCita(@PathVariable Long id,
                                                      @Valid @RequestBody CitaUpdateDTO dto) {

        logger.info("Actualizando cita con ID {}", id);

        dto.setId(id);

        CitaDTO updated = citaService.update(dto);

        logger.info("Cita con ID {} actualizada con éxito.", id);

        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina una cita por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCita(@PathVariable Long id) {

        logger.info("Eliminando cita con id: {}", id);

        citaService.delete(id);

        logger.info("Cita con ID {} eliminada con éxito.", id);

        return ResponseEntity.noContent().build();
    }
}
