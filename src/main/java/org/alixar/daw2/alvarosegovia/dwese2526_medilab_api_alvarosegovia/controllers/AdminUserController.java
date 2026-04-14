package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.UserCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.UserDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.UserUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Admin - Usuarios", description = "Gestión de usuarios para el panel de administración")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Listar usuarios",
            description = "Lista paginada de usuarios. Filtro opcional por rol (ej. ROLE_MEDICO, ROLE_TECNICO, ROLE_PACIENTE).")
    public ResponseEntity<Page<UserDetailDTO>> list(
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UserDetailDTO> page = (role != null && !role.isBlank())
                ? userService.listByRole(role, pageable)
                : userService.listAllWithProfile(pageable);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de usuario",
            description = "Devuelve el detalle completo de un usuario incluyendo perfil y roles.")
    public ResponseEntity<UserDetailDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "Crear usuario",
            description = "Crea un nuevo usuario con sus roles.")
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO dto) {
        Long newId = userService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario",
            description = "Actualiza datos de cuenta y roles de un usuario existente.")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        dto.setId(id);
        userService.update(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario",
            description = "Elimina un usuario por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
