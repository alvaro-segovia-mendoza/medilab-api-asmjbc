package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para el formulario de edición del perfil de usuario.
 *
 * Se utiliza tanto para crear el perfil (si no existe)
 * como para actualizarlo (si ya existe).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    /** ID del usuario autenticado (se usa para buscar/crear el perfil) */
    private Long userId;

    /** Email solo lectura, informativo en la vista */
    private String email;

    /** VARCHAR(60) NOT NULL */
    @Size(max = 60, message = "{validation.user.profile.firstName.size}")
    private String firstName;

    /** VARCHAR(80) NOT NULL */
    @Size(max = 80, message = "{validation.user.profile.lastName.size}")
    private String lastName;

    /** VARCHAR(30) NULL */
    @Size(max = 30, message = "{validation.user.profile.phoneNumber.size}")
    private String phoneNumber;

    /** VARCHAR(255) NULL - Ruta/URL de la imagen del perfil */
    @Size(max = 255, message = "{validation.user.profile.profileImage.size}")
    private String profileImage;

    /** VARCHAR(500) NULL - Pequeña descripción / biografía */
    @Size(max = 500, message = "{validation.user.profile.bio.size}")
    private String bio;

    /** VARCHAR(10) NULL - Código idioma/locale (es_ES, en_ES...) */
    @Size(max = 10, message = "{validation.user.profile.locale.size}")
    private String locale;

    /** VARCHAR(20) NULL UNIQUE */
    @Size(max = 20, message = "{validation.user.profile.dni.size}")
    private String dni;

    /** DATE NULL */
    private LocalDate dateOfBirth;

    /** VARCHAR(150) NULL */
    @Size(max = 150, message = "{validation.user.profile.address.size}")
    private String address;

    /** VARCHAR(50) NULL */
    @Size(max = 50, message = "{validation.user.profile.city.size}")
    private String city;

    /** VARCHAR(50) NULL */
    @Size(max = 50, message = "{validation.user.profile.province.size}")
    private String province;

}
