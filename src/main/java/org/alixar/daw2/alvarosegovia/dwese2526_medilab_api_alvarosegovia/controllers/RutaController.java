package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.RutaUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Gestión de rutas de trailers")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    @GetMapping
    @Operation(summary = "Listar rutas", description = "Devuelve todas las rutas.")
    public ResponseEntity<List<RutaDTO>> list() {
        return ResponseEntity.ok(rutaService.list());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar rutas activas", description = "Devuelve las rutas activas disponibles para reserva.")
    public ResponseEntity<List<RutaDTO>> listActivas() {
        return ResponseEntity.ok(rutaService.listActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de ruta", description = "Devuelve el detalle de una ruta.")
    public ResponseEntity<RutaDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "Crear ruta", description = "Crea una nueva ruta asignada a un trailer.")
    public ResponseEntity<RutaDTO> create(@Valid @RequestBody RutaCreateDTO dto) {
        RutaDTO created = rutaService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ruta", description = "Actualiza una ruta existente.")
    public ResponseEntity<RutaDTO> update(@PathVariable Long id, @Valid @RequestBody RutaUpdateDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(rutaService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ruta", description = "Elimina una ruta por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rutaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
