package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.logistics.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.logistics.ParadaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestionar paradas logisticas y su disponibilidad.
 */
@RestController
@RequestMapping("/api/paradas")
@Tag(name = "Paradas", description = "Gestión de paradas y disponibilidad de slots")
public class ParadaController {

    private static final Logger logger = LoggerFactory.getLogger(ParadaController.class);

    @Autowired
    private ParadaService paradaService;

    /**
     * Lista paradas con paginacion.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de paradas.
     */
    @GetMapping
    @Operation(summary = "Listar paradas", description = "Devuelve las paradas paginadas y ordenables.")
    public ResponseEntity<Page<ParadaDTO>> list(
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.info("Listando paradas paginadas");
        return ResponseEntity.ok(paradaService.listPaged(pageable));
    }

    /**
     * Recupera el detalle operativo de una parada.
     *
     * @param id identificador de la parada.
     * @return detalle de la parada.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Detalle de parada", description = "Devuelve el detalle operativo de una parada, incluyendo municipio y provincia.")
    public ResponseEntity<ParadaDTO> getDetail(@PathVariable Long id) {
        logger.info("Consultando detalle de la parada {}", id);
        return ResponseEntity.ok(paradaService.getDetail(id));
    }

    /**
     * Lista las paradas asociadas a una ruta.
     *
     * @param rutaId identificador de la ruta.
     * @return lista ordenada de paradas.
     */
    @GetMapping("/ruta/{rutaId}")
    @Operation(summary = "Listar paradas por ruta", description = "Devuelve las paradas de una ruta ordenadas por fecha y orden.")
    public ResponseEntity<List<ParadaDTO>> listByRuta(@PathVariable Long rutaId) {
        logger.info("Listando paradas de la ruta {}", rutaId);
        return ResponseEntity.ok(paradaService.listByRuta(rutaId));
    }

    /**
     * Lista paradas activas para una fecha concreta.
     *
     * @param fecha fecha operativa a consultar.
     * @return paradas activas de la fecha indicada.
     */
    @GetMapping("/activas")
    @Operation(summary = "Listar paradas activas por fecha", description = "Devuelve las paradas activas de una fecha concreta.")
    public ResponseEntity<List<ParadaDTO>> listActivasByFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        logger.info("Listando paradas activas para fecha={}", fecha);
        return ResponseEntity.ok(paradaService.listActiveByFecha(fecha));
    }

    /**
     * Devuelve la disponibilidad agregada de una parada.
     *
     * @param id identificador de la parada.
     * @return resumen de slots y capacidad disponible.
     */
    @GetMapping("/{id}/slots-disponibles")
    @Operation(summary = "Consultar slots disponibles", description = "Devuelve la disponibilidad agregada de una parada a partir de slots persistidos. Cada franja indica slots libres, técnicos disponibles y los `slotIdsDisponibles` que pueden reservarse.")
    public ResponseEntity<DisponibilidadParadaDTO> getDisponibilidad(@PathVariable Long id) {
        logger.info("Consultando disponibilidad de la parada {}", id);
        return ResponseEntity.ok(paradaService.getDisponibilidad(id));
    }

    /**
     * Crea una nueva parada y reconcilia sus slots reservables.
     *
     * @param dto datos de alta de la parada.
     * @return parada creada con cabecera Location.
     */
    @PostMapping
    @Operation(summary = "Crear parada", description = "Crea una nueva parada para una ruta con horario operativo fijo de 09:00 a 15:00 y reconcilia sus slots reservables.")
    public ResponseEntity<ParadaDTO> create(@Valid @RequestBody ParadaCreateDTO dto) {
        logger.info("Creando parada para rutaId={} en fecha={}", dto.getRutaId(), dto.getFecha());
        ParadaDTO created = paradaService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Actualiza una parada existente.
     *
     * @param id identificador de la parada.
     * @param dto datos actualizados de la parada.
     * @return parada actualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar parada", description = "Actualiza una parada existente con horario operativo fijo de 09:00 a 15:00. Si cambia la fecha o la capacidad, reconcilia los slots siempre que no existan citas activas afectadas.")
    public ResponseEntity<ParadaDTO> update(@PathVariable Long id, @Valid @RequestBody ParadaUpdateDTO dto) {
        logger.info("Actualizando parada {}", id);
        dto.setId(id);
        return ResponseEntity.ok(paradaService.update(dto));
    }

    /**
     * Elimina una parada por su identificador.
     *
     * @param id identificador de la parada.
     * @return respuesta vacia con estado 204.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar parada", description = "Elimina una parada por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Eliminando parada {}", id);
        paradaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
