package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para consultar los roles disponibles en el panel de administracion.
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Admin - Roles", description = "Consulta de roles disponibles para asignación de usuarios")
public class AdminRoleController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRoleController.class);

    @Autowired
    private UserService userService;

    /**
     * Devuelve el catalogo de roles disponibles para asignacion administrativa.
     *
     * @return respuesta con la lista de roles resumidos.
     */
    @GetMapping
    @Operation(summary = "Listar roles", description = "Devuelve todos los roles del sistema (id, name, displayName).")
    public ResponseEntity<List<RoleDTO>> listRoles() {
        logger.info("Listando roles disponibles para administracion");
        List<RoleDTO> roles = userService.listRoles().stream()
                .map(r -> new RoleDTO(r.getId(), r.getName(), r.getDisplayName()))
                .toList();
        return ResponseEntity.ok(roles);
    }

    public record RoleDTO(Long id, String name, String displayName) {}
}
