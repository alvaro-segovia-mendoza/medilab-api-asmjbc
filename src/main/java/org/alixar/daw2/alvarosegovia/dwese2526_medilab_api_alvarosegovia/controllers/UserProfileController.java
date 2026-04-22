package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfileDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfilePatchDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


/**
 * Controlador MVC encargado de la gestión del perfil de usuario.
 * <p>
 * Permite visualizar y actualizar los datos del perfil, incluyendo
 * la subida y gestión de la imagen de perfil.
 * </p>
 *
 * Ruta base: /profile
 */
@RestController
@RequestMapping("/api/profile")
@Validated
public class UserProfileController {

    /** Logger para trazas y depuración */
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    /** Servicio de lógica de negocio para el perfil de usuario */
    @Autowired
    private UserProfileService userProfileService;


    /**
     * GET API: devuelve el perfil para el usuario autenticado.
     * Equivale a "mostrar formulario", pero en API devuelve datos.
     */
    @GetMapping
    public ResponseEntity<UserProfileDTO> getMyProfile(Principal principal) {
        String email = principal.getName();
        logger.info("getMyProfile email: {}", email);

        UserProfileDTO dto = userProfileService.getFormByEmail(email);
        return ResponseEntity.ok(dto);
    }

    /**
     * GET API (admin): devuelve el perfil de cualquier usuario por su ID.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable Long userId) {
        logger.info("getUserProfileById userId: {}", userId);
        UserProfileDTO dto = userProfileService.getFormById(userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Actualiza el perfil del usuario autenticado (PATCH).
     *
     * <p>Consume <b>multipart/form-data</b> con:</p>
     * <ul>
     *     <li><b>profile</b>: JSON con los campos a modificar (solo se actualizan los presentes).</li>
     *     <li><b>profileImageFile</b> (opcional): nueva imagen de perfil.</li>
     * </ul>
     *
     * <p>El usuario se identifica a partir del {@link Principal}.</p>
     *
     * @param patchDTO datos parciales del perfil a aplicar
     * @param profileImageFile imagen de perfil opcional
     * @param principal usuario autenticado
     * @return perfil actualizado
     */
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDTO> patchMyProfile(
            @Valid @ModelAttribute UserProfilePatchDTO patchDTO,
            Principal principal
    ) {
        String email = principal.getName();
        logger.info("patchMyProfile email: {}", email);

        userProfileService.updateProfile(email, patchDTO, patchDTO.getProfileImageFile());

        UserProfileDTO dto = userProfileService.getFormByEmail(email);
        return ResponseEntity.ok(dto);
    }


}
