package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link User}.
 * <p>
 * Extiende {@link JpaRepository} para proporcionar operaciones CRUD
 * estándar y consultas basadas en convenciones de Spring Data.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Localiza un usuario por email (ignorando mayúsculas/minúsculas) y asegura que sus roles
     * queden cargados en la misma consulta.
     *
     * @param email email del usuario (usado como identificador/username del sistema).
     * @return {@link Optional} con el usuario y sus roles; {@code Optional.empty()} si no existe.
     */
    @EntityGraph(attributePaths = {"roles", "profile"})
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Comprueba si existe un usuario con el email indicado.
     *
     * @param email email del usuario
     * @return {@code true} si existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Comprueba si existe un usuario con el email indicado, ignorando mayúsculas/minúsculas.
     *
     * @param email email del usuario
     * @return {@code true} si existe un usuario con ese email
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Recupera usuarios paginados cargando roles y perfil en la misma consulta.
     *
     * @param pageable información de paginación y ordenación
     * @return página de usuarios
     */
    @Override
    @EntityGraph(attributePaths = {"roles", "profile"})
    Page<User> findAll(Pageable pageable);

    /**
     * Recupera un {@link User} por su identificador, cargando de forma anticipada
     * la entidad UserProfile asociada.
     * <p>
     * Se utiliza {@code LEFT JOIN FETCH} para:
     * <ul>
     *   <li>Evitar el problema de {@code LazyInitializationException}</li>
     *   <li>Cargar el perfil en la misma consulta</li>
     *   <li>Permitir que el usuario sea devuelto incluso si no tiene perfil asociado</li>
     * </ul>
     *
     * @param id identificador único del usuario
     * @return un {@link Optional} que contiene el usuario con su perfil cargado,
     *         o vacío si no existe un usuario con ese id
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") Long id);

    /**
     * Recupera un usuario por id con sus roles y perfil en una sola carga.
     * Se usa en operaciones de borrado para que Hibernate gestione correctamente
     * la tabla intermedia de roles y la relación 1:1 con UserProfile.
     *
     * @param id identificador del usuario
     * @return Optional con el usuario cargado junto a roles y perfil
     */
    @EntityGraph(attributePaths = {"roles", "profile"})
    Optional<User> findWithRolesAndProfileById(Long id);

    /**
     * Comprueba si existe algún {@link User} con el email indicado,
     * excluyendo al usuario cuyo identificador se pasa como parámetro.
     * <p>
     * Este método se utiliza principalmente en operaciones de actualización
     * para evitar falsos positivos al validar emails duplicados cuando
     * el usuario mantiene su propio email sin cambios.
     *
     * @param email email a comprobar
     * @param id identificador del usuario que debe excluirse de la búsqueda
     * @return {@code true} si existe otro usuario con ese email,
     *         {@code false} en caso contrario
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Comprueba si existe algún {@link User} con el email indicado,
     * excluyendo al usuario cuyo identificador se pasa como parámetro
     * e ignorando mayúsculas/minúsculas.
     *
     * @param email email a comprobar
     * @param id identificador del usuario que debe excluirse de la búsqueda
     * @return {@code true} si existe otro usuario con ese email,
     *         {@code false} en caso contrario
     */
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @EntityGraph(attributePaths = {"roles", "profile"})
    List<User> findDistinctByRolesNameOrderByIdAsc(String roleName);

    @EntityGraph(attributePaths = {"roles", "profile"})
    Page<User> findDistinctByRolesName(String roleName, Pageable pageable);

}
