package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Role;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrato de aplicacion para la gestion de usuarios y roles del sistema.
 */
public interface UserService {

    /**
     * Lista usuarios resumidos con paginacion.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de usuarios.
     */
    Page<UserDTO> list(Pageable pageable);

    /**
     * Lista usuarios con informacion de perfil.
     *
     * @param pageable configuracion de pagina y orden.
     * @return pagina de usuarios con detalle ampliado.
     */
    Page<UserDetailDTO> listAllWithProfile(Pageable pageable);

    /**
     * Lista usuarios filtrados por rol.
     *
     * @param roleName nombre tecnico del rol.
     * @param pageable configuracion de pagina y orden.
     * @return pagina de usuarios que cumplen el rol indicado.
     */
    Page<UserDetailDTO> listByRole(String roleName, Pageable pageable);

    /**
     * Busca pacientes por texto libre.
     *
     * @param query texto de busqueda.
     * @param pageable configuracion de pagina y orden.
     * @return pagina de pacientes coincidentes.
     */
    Page<UserDetailDTO> searchPatients(String query, Pageable pageable);

    /**
     * Obtiene los datos de un usuario para editarlo.
     *
     * @param id identificador del usuario.
     * @return DTO preparado para edicion.
     */
    UserUpdateDTO getForEdit(Long id);

    /**
     * Crea un nuevo usuario.
     *
     * @param dto datos de alta.
     * @return identificador del usuario creado.
     */
    Long create(UserCreateDTO dto);

    /**
     * Actualiza un usuario existente.
     *
     * @param dto datos actualizados del usuario.
     */
    void update(UserUpdateDTO dto);

    /**
     * Elimina un usuario existente.
     *
     * @param id identificador del usuario.
     */
    void delete(Long id);

    /**
     * Obtiene el detalle completo de un usuario.
     *
     * @param id identificador del usuario.
     * @return detalle del usuario.
     */
    UserDetailDTO getDetail(Long id);

    /**
     * Lista todos los roles configurados en el sistema.
     *
     * @return roles disponibles.
     */
    List<Role> listRoles();

    /**
     * Registra un nuevo paciente con credenciales locales.
     *
     * @param email email del paciente.
     * @param rawPassword contrasena en claro antes de codificar.
     * @return usuario persistido.
     */
    User registerPatient(String email, String rawPassword);

    /**
     * Busca un usuario OAuth2 por email o lo crea si todavia no existe.
     *
     * @param email email normalizado del usuario.
     * @param displayName nombre visible recibido del proveedor.
     * @return usuario persistido o encontrado.
     */
    User findOrCreateOAuth2User(String email, String displayName);

    /**
     * Recupera un usuario por email.
     *
     * @param email email a buscar.
     * @return usuario encontrado.
     */
    User getByEmail(String email);

    /**
     * Actualiza la contrasena de un usuario existente.
     *
     * @param userId identificador del usuario.
     * @param rawPassword nueva contrasena en claro antes de codificar.
     */
    void updatePassword(Long userId, String rawPassword);
}
