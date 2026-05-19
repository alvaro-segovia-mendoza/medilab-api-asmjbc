package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * DTO usado en el dominio de usuarios para transportar datos de UserProfilePatchDTO.
 */
@Data
public class UserProfilePatchDTO {

    @Size(max = 60, message = "{validation.user.profile.firstName.size}")
    private String firstName;

    @Size(max = 80, message = "{validation.user.profile.lastName.size}")
    private String lastName;

    @Size(max = 30, message = "{validation.user.profile.phoneNumber.size}")
    private String phoneNumber;

    @Size(max = 500, message = "{validation.user.profile.bio.size}")
    private String bio;

    @Size(max = 10, message = "{validation.user.profile.locale.size}")
    private String locale;

    @Size(max = 20, message = "{validation.user.profile.dni.size}")
    private String dni;

    private LocalDate dateOfBirth;

    @Size(max = 150, message = "{validation.user.profile.address.size}")
    private String address;

    @Size(max = 50, message = "{validation.user.profile.city.size}")
    private String city;

    @Size(max = 50, message = "{validation.user.profile.province.size}")
    private String province;

    @Schema(description = "Imagen de perfil (jpg/png, máx 2 MB)", type = "string", format = "binary")
    private MultipartFile profileImageFile;

}
