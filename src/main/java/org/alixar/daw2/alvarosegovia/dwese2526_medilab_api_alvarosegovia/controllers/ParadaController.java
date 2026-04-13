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
    @Operation(summary = "Listar paradas", description = "Devuelve todas las paradas.")
    public ResponseEntity<List<ParadaDTO>> list() {
        return ResponseEntity.ok(paradaService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de parada", description = "Devuelve el detalle de una parada.")
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
    @Operation(summary = "Consultar slots disponibles", description = "Calcula los slots disponibles de una parada según su ventana horaria y la disponibilidad real de técnicos.")
    public ResponseEntity<DisponibilidadParadaDTO> getDisponibilidad(@PathVariable Long id) {
        return ResponseEntity.ok(paradaService.getDisponibilidad(id));
    }

    @PostMapping
    @Operation(summary = "Crear parada", description = "Crea una nueva parada para una ruta.")
    public ResponseEntity<ParadaDTO> create(@Valid @RequestBody ParadaCreateDTO dto) {
        ParadaDTO created = paradaService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar parada", description = "Actualiza una parada existente.")
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
