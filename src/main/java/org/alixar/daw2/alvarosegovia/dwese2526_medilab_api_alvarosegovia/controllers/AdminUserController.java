package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
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

/**
 * Controlador REST para gestionar usuarios desde el panel de administracion.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Admin - Usuarios", description = "Gestión de usuarios para el panel de administración")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserService userService;

    /**
     * Lista usuarios del sistema con paginacion y filtro opcional por rol.
     *
     * @param role nombre tecnico del rol a filtrar.
     * @param pageable configuracion de pagina y orden.
     * @return pagina de usuarios con perfil.
     */
    @GetMapping
    @Operation(summary = "Listar usuarios",
            description = "Lista paginada de usuarios. Filtro opcional por rol (ej. ROLE_MEDICO, ROLE_TECNICO, ROLE_PACIENTE).")
    public ResponseEntity<Page<UserDetailDTO>> list(
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.info("Listando usuarios para administracion con filtroRol={}", role);

        Page<UserDetailDTO> page = (role != null && !role.isBlank())
                ? userService.listByRole(role, pageable)
                : userService.listAllWithProfile(pageable);

        return ResponseEntity.ok(page);
    }

    /**
     * Recupera el detalle completo de un usuario.
     *
     * @param id identificador del usuario.
     * @return detalle del usuario solicitado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Detalle de usuario",
            description = "Devuelve el detalle completo de un usuario incluyendo perfil y roles.")
    public ResponseEntity<UserDetailDTO> getDetail(@PathVariable Long id) {
        logger.info("Consultando detalle del usuario {}", id);
        return ResponseEntity.ok(userService.getDetail(id));
    }

    /**
     * Crea un nuevo usuario administrativo.
     *
     * @param dto datos de alta del usuario.
     * @return respuesta 201 con la ubicacion del nuevo recurso.
     */
    @PostMapping
    @Operation(summary = "Crear usuario",
            description = "Crea un nuevo usuario con sus roles.")
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO dto) {
        logger.info("Creando usuario administrativo con email={}", dto.getEmail());
        Long newId = userService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id identificador del usuario.
     * @param dto datos actualizados.
     * @return respuesta vacia con estado 204.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario",
            description = "Actualiza datos de cuenta y roles de un usuario existente.")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        logger.info("Actualizando usuario administrativo {}", id);
        dto.setId(id);
        userService.update(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un usuario por su identificador.
     *
     * @param id identificador del usuario.
     * @return respuesta vacia con estado 204.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario",
            description = "Elimina un usuario por id.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Eliminando usuario administrativo {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
