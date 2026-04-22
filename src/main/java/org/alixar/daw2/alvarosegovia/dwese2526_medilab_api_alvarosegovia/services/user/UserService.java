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

public interface UserService {

    Page<UserDTO> list(Pageable pageable);

    Page<UserDetailDTO> listAllWithProfile(Pageable pageable);

    Page<UserDetailDTO> listByRole(String roleName, Pageable pageable);

    UserUpdateDTO getForEdit(Long id);

    Long create(UserCreateDTO dto);

    void update(UserUpdateDTO dto);

    void delete(Long id);

    UserDetailDTO getDetail(Long id);

    List<Role> listRoles();

    User registerPatient(String email, String rawPassword);

    User getByEmail(String email);

    void updatePassword(Long userId, String rawPassword);
}
