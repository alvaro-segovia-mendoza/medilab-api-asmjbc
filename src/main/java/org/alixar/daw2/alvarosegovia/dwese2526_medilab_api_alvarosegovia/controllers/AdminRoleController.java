package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Admin - Roles", description = "Consulta de roles disponibles para asignación de usuarios")
public class AdminRoleController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Listar roles", description = "Devuelve todos los roles del sistema (id, name, displayName).")
    public ResponseEntity<List<RoleDTO>> listRoles() {
        List<RoleDTO> roles = userService.listRoles().stream()
                .map(r -> new RoleDTO(r.getId(), r.getName(), r.getDisplayName()))
                .toList();
        return ResponseEntity.ok(roles);
    }

    public record RoleDTO(Long id, String name, String displayName) {}
}
