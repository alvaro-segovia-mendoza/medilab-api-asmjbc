package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TecnicoUpdateDTO;
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
 * Controlador que maneja las operaciones CRUD para la entidad 'Tecnico'.
 * Utiliza 'TecnicoDAO' para interactuar con la base de datos.
 */
@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    // Logger para registrar eventos importantes en el Controller
    private static final Logger logger = LoggerFactory.getLogger(TecnicoController.class);

    @Autowired
    private TecnicoService tecnicoService;

    @Autowired
    private MessageSource messageSource;


    /**
     * Lista paginada de tecnicos en JSON usando el Pageable estándar de Spring data.
     *
     * Ejemplos:
     *   GET /api/tecnicos?page=0&size=10&sort=name,asc
     *   GET /api/tecnicos?sort=name,desc
     */
    @Operation(summary = "Lista paginada de tecnicos", description = "Devuelve una página de tecnicos en formato JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tecnicos devuelta correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<TecnicoDTO>> getTecnicos(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        logger.info("Listando tecnicos (REST) page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<TecnicoDTO> page = tecnicoService.list(pageable);

        logger.info("Se han cargado {} tecnicos en la página {}.", page.getNumberOfElements(), page.getNumber());

        return ResponseEntity.ok(page);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TecnicoDTO>> getAllTecnicos() {
        logger.info("Solicitando la lista de todas las tecnicos...");
        List<TecnicoDTO> tecnicos = tecnicoService.getAllTecnicos();
        return ResponseEntity.ok(tecnicos);
    }


    /**
     * Devuelve el detalle de una técnico por ID (incluyendo citas asociadas) en JSON.
     *
     * Ejemplo:
     *   GET /api/tecnicos/10
     */
    @Operation(summary = "Detalle de una técnico por ID", description = "Incluye citas asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Técnico encontrada"),
            @ApiResponse(responseCode = "404", description = "Técnico no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TecnicoDetailDTO> getTecnicoById(@PathVariable Long id) {
        logger.info("Retornando tecnico de id {}", id);
        TecnicoDetailDTO tecnicoDTO = tecnicoService.getDetail(id);
        return ResponseEntity.ok(tecnicoDTO);
    }


    /**
     * Crea una nueva técnico.
     *
     * <p>
     *     Entrada: JSON (TecnicoCreateDTO). Salida: 201 Created + Location + TecnicoDTO.
     * </p>
     */
    @Operation(summary = "Crea una nueva técnico", description = "Entrada JSON: TecnicoCreateDTO. Salida: 201 Created + Location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Técnico creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la creación"),
            @ApiResponse(responseCode = "409", description = "Código de técnico duplicado")
    })
    @PostMapping
    public ResponseEntity<TecnicoDTO> createTecnico(@Valid @RequestBody TecnicoCreateDTO dto) {
        logger.info("Creando tecnico {}", dto);

        TecnicoDTO created = tecnicoService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }


    /**
     * Actualiza una técnico existente.
     * Entrada: JSON (TecnicoUpdateDTO). Salida: 200 OK + TecnicoDTO actualizada.
     *
     * Errores: (vía @RestControllerAdvice)
     * - 400 Bad request: validación DTO
     * - 404 Not Found: técnico no existe
     * - 409 Conflict: código duplicado
     * - 500 Internal Server Error: error inesperado
     */
    @Operation(
            summary = "Actualiza una técnico por ID",
            description = "Actualiza los datos de una técnico existente. Si no existe, devuelve 404 Not Found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Técnico actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Técnico no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado para actualizar la técnico")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TecnicoDTO> updateTecnico(@PathVariable Long id,
                                                  @Valid @RequestBody TecnicoUpdateDTO dto) {

        logger.info("Actualizando técnico con ID {}", id);

        // Buena práctica: asegurar consistencia entre path y body
        dto.setId(id);

        TecnicoDTO updated =  tecnicoService.update(dto);

        logger.info("Técnico con ID {} actualizada con éxito.", id);

        return ResponseEntity.ok(updated);
    }


    /**
     * Elimina una técnico por ID.
     * <p>
     * REST: DELETE /api/tecnicos/{id}
     * - 204 No content si se elimina correctamente
     * - 404 Not found si no existe (ResourceNotFoundException)
     * - 500 Internal Server Error si ocurre un error inesperado
     * </p>
     */
    @Operation(
            summary = "Elimina una técnico por ID",
            description = "Si la técnico existe, se elimina. Si no existe, devuelve 404 Not Found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Técnico eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Técnico no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar la técnico")
    })
    @DeleteMapping("/{id}")
//@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTecnico(@PathVariable Long id) {
        logger.info("Eliminando técnico con id: {}", id);

        // 1) Delegamos en el servicio:
        //      - si existe: elimina
        //      - si no existe: lanza ResourceNotFoundException (se convertirá a 404 en el @RestControllerAdvice)
        tecnicoService.delete(id);

        logger.info("Técnico con ID {} eliminado con éxito.", id);

        // 2) En REST, lo habitual en un DELETE correcto es 204 No Content (sin body)
        return ResponseEntity.noContent().build();
    }
}
