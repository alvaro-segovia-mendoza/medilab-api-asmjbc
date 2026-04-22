package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO básico para representar usuarios relacionados en otras vistas de detalle.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
