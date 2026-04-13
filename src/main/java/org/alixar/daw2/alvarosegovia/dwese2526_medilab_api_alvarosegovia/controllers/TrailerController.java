package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.TrailerUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.TrailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trailers")
@Tag(name = "Trailers", description = "Gestión de trailers móviles")
public class TrailerController {

    @Autowired
    private TrailerService trailerService;

    @GetMapping
    @Operation(summary = "Listar trailers", description = "Devuelve todos los trailers.")
    public ResponseEntity<List<TrailerDTO>> list() {
        return ResponseEntity.ok(trailerService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de trailer", description = "Devuelve el detalle de un trailer.")
    public ResponseEntity<TrailerDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(trailerService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "Crear trailer", description = "Crea un nuevo trailer.")
    public ResponseEntity<TrailerDTO> create(@Valid @RequestBody TrailerCreateDTO dto) {
        TrailerDTO created = trailerService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar trailer", description = "Actualiza un trailer existente.")
    public ResponseEntity<TrailerDTO> update(@PathVariable Long id, @Valid @RequestBody TrailerUpdateDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(trailerService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar trailer", description = "Elimina un trailer por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trailerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
