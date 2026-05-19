package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfileDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfilePatchDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrato de aplicacion para consultar y actualizar perfiles de usuario.
 */
public interface UserProfileService {

    /**
     * Obtiene el formulario de perfil asociado a un email.
     *
     * @param email email del usuario.
     * @return DTO de perfil para formulario.
     */
    UserProfileDTO getFormByEmail(String email);

    /**
     * Obtiene el formulario de perfil asociado a un usuario.
     *
     * @param userId identificador del usuario.
     * @return DTO de perfil para formulario.
     */
    UserProfileDTO getFormById(Long userId);

    /**
     * Aplica cambios parciales sobre el perfil del usuario y su imagen.
     *
     * @param email email autenticado del usuario.
     * @param profileDTO datos parciales del perfil.
     * @param profileImageFile fichero opcional de imagen.
     */
    void updateProfile(String email, UserProfilePatchDTO profileDTO, MultipartFile profileImageFile);
}
