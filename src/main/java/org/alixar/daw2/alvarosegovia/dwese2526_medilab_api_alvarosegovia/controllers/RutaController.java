package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.RutaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics.RutaService;
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
import java.util.List;

/**
 * Controlador REST para gestionar rutas operativas de los trailers.
 */
@RestController
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Gestión de rutas de trailers")
public class RutaController {

    private static final Logger logger = LoggerFactory.getLogger(RutaController.class);

    @Autowired
    private RutaService rutaService;

    /**
     * Lista rutas con paginacion.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de rutas.
     */
    @GetMapping
    @Operation(summary = "Listar rutas", description = "Devuelve las rutas paginadas y ordenables.")
    public ResponseEntity<Page<RutaDTO>> list(
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.info("Listando rutas paginadas");
        return ResponseEntity.ok(rutaService.listPaged(pageable));
    }

    /**
     * Lista las rutas activas disponibles para reserva.
     *
     * @return lista de rutas activas.
     */
    @GetMapping("/activas")
    @Operation(summary = "Listar rutas activas", description = "Devuelve las rutas activas disponibles para reserva.")
    public ResponseEntity<List<RutaDTO>> listActivas() {
        logger.info("Listando rutas activas");
        return ResponseEntity.ok(rutaService.listActive());
    }

    /**
     * Recupera el detalle de una ruta concreta.
     *
     * @param id identificador de la ruta.
     * @return detalle de la ruta.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Detalle de ruta", description = "Devuelve el detalle de una ruta.")
    public ResponseEntity<RutaDTO> getDetail(@PathVariable Long id) {
        logger.info("Consultando detalle de la ruta {}", id);
        return ResponseEntity.ok(rutaService.getDetail(id));
    }

    /**
     * Crea una nueva ruta operativa.
     *
     * @param dto datos de alta de la ruta.
     * @return ruta creada con cabecera Location.
     */
    @PostMapping
    @Operation(summary = "Crear ruta", description = "Crea una nueva ruta asignada a un trailer.")
    public ResponseEntity<RutaDTO> create(@Valid @RequestBody RutaCreateDTO dto) {
        logger.info("Creando ruta con nombre={}", dto.getNombre());
        RutaDTO created = rutaService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Actualiza una ruta existente.
     *
     * @param id identificador de la ruta.
     * @param dto datos actualizados de la ruta.
     * @return ruta actualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ruta", description = "Actualiza una ruta existente. No permite desactivar la ruta ni cambiar su tráiler si existen citas activas futuras.")
    public ResponseEntity<RutaDTO> update(@PathVariable Long id, @Valid @RequestBody RutaUpdateDTO dto) {
        logger.info("Actualizando ruta {}", id);
        dto.setId(id);
        return ResponseEntity.ok(rutaService.update(dto));
    }

    /**
     * Elimina una ruta por id.
     *
     * @param id identificador de la ruta.
     * @return respuesta vacia con estado 204.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ruta", description = "Elimina una ruta por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Eliminando ruta {}", id);
        rutaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
