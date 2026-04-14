package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.DisponibilidadParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.ParadaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.ParadaService;
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

@RestController
@RequestMapping("/api/paradas")
@Tag(name = "Paradas", description = "Gestión de paradas y disponibilidad de slots")
public class ParadaController {

    @Autowired
    private ParadaService paradaService;

    @GetMapping
    @Operation(summary = "Listar paradas", description = "Devuelve las paradas paginadas y ordenables.")
    public ResponseEntity<Page<ParadaDTO>> list(
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(paradaService.listPaged(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de parada", description = "Devuelve el detalle operativo de una parada, incluyendo municipio y provincia.")
    public ResponseEntity<ParadaDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(paradaService.getDetail(id));
    }

    @GetMapping("/ruta/{rutaId}")
    @Operation(summary = "Listar paradas por ruta", description = "Devuelve las paradas de una ruta ordenadas por fecha y orden.")
    public ResponseEntity<List<ParadaDTO>> listByRuta(@PathVariable Long rutaId) {
        return ResponseEntity.ok(paradaService.listByRuta(rutaId));
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar paradas activas por fecha", description = "Devuelve las paradas activas de una fecha concreta.")
    public ResponseEntity<List<ParadaDTO>> listActivasByFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(paradaService.listActiveByFecha(fecha));
    }

    @GetMapping("/{id}/slots-disponibles")
    @Operation(summary = "Consultar slots disponibles", description = "Devuelve la disponibilidad agregada de una parada a partir de slots persistidos. Cada franja incluye los `slotIdsDisponibles` que pueden reservarse.")
    public ResponseEntity<DisponibilidadParadaDTO> getDisponibilidad(@PathVariable Long id) {
        return ResponseEntity.ok(paradaService.getDisponibilidad(id));
    }

    @PostMapping
    @Operation(summary = "Crear parada", description = "Crea una nueva parada para una ruta y genera automáticamente sus slots reservables.")
    public ResponseEntity<ParadaDTO> create(@Valid @RequestBody ParadaCreateDTO dto) {
        ParadaDTO created = paradaService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar parada", description = "Actualiza una parada existente. Si cambia la planificación horaria o la capacidad, se regeneran los slots siempre que no existan citas activas asociadas.")
    public ResponseEntity<ParadaDTO> update(@PathVariable Long id, @Valid @RequestBody ParadaUpdateDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(paradaService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar parada", description = "Elimina una parada por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paradaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
