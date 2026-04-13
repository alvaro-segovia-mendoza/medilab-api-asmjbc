package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfilePatchDTO {

    @Size(max = 60, message = "{msg.userProfile.firstName.size}")
    private String firstName;

    @Size(max = 80, message = "{msg.userProfile.lastName.size}")
    private String lastName;

    @Size(max = 30, message = "{msg.userProfile.phoneNumber.size}")
    private String phoneNumber;

    @Size(max = 500, message = "{msg.userProfile.bio.size}")
    private String bio;

    @Size(max = 10, message = "{msg.userProfile.locale.size}")
    private String locale;

    @Size(max = 20, message = "{msg.userProfile.dni.size}")
    private String dni;

    private LocalDate dateOfBirth;

    @Size(max = 150, message = "{msg.userProfile.address.size}")
    private String address;

    @Size(max = 50, message = "{msg.userProfile.city.size}")
    private String city;

    @Size(max = 50, message = "{msg.userProfile.province.size}")
    private String province;

}
