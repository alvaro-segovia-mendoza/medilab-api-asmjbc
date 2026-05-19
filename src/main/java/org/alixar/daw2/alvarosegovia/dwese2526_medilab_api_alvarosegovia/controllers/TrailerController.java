package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.TrailerUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics.TrailerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador REST para gestionar trailers y su catalogo operativo.
 */
@RestController
@RequestMapping("/api/trailers")
@Tag(name = "Trailers", description = "Gestión de trailers móviles")
public class TrailerController {

    private static final Logger logger = LoggerFactory.getLogger(TrailerController.class);

    @Autowired
    private TrailerService trailerService;

    /**
     * Lista trailers con paginacion.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de trailers.
     */
    @GetMapping
    @Operation(summary = "Listar trailers", description = "Devuelve los trailers paginados y ordenables.")
    public ResponseEntity<Page<TrailerDTO>> list(
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.info("Listando trailers paginados");
        return ResponseEntity.ok(trailerService.listPaged(pageable));
    }

    /**
     * Recupera el detalle de un trailer concreto.
     *
     * @param id identificador del trailer.
     * @return detalle del trailer.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Detalle de trailer", description = "Devuelve el detalle de un trailer.")
    public ResponseEntity<TrailerDTO> getDetail(@PathVariable Long id) {
        logger.info("Consultando detalle del trailer {}", id);
        return ResponseEntity.ok(trailerService.getDetail(id));
    }

    /**
     * Crea un nuevo trailer.
     *
     * @param dto datos de alta del trailer.
     * @return trailer creado con cabecera Location.
     */
    @PostMapping
    @Operation(summary = "Crear trailer", description = "Crea un nuevo trailer.")
    public ResponseEntity<TrailerDTO> create(@Valid @RequestBody TrailerCreateDTO dto) {
        logger.info("Creando trailer con codigo={}", dto.getCodigo());
        TrailerDTO created = trailerService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Actualiza un trailer existente.
     *
     * @param id identificador del trailer.
     * @param dto datos actualizados del trailer.
     * @return trailer actualizado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar trailer", description = "Actualiza un trailer existente.")
    public ResponseEntity<TrailerDTO> update(@PathVariable Long id, @Valid @RequestBody TrailerUpdateDTO dto) {
        logger.info("Actualizando trailer {}", id);
        dto.setId(id);
        return ResponseEntity.ok(trailerService.update(dto));
    }

    /**
     * Elimina un trailer por su identificador.
     *
     * @param id identificador del trailer.
     * @return respuesta vacia con estado 204.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar trailer", description = "Elimina un trailer por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Eliminando trailer {}", id);
        trailerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
