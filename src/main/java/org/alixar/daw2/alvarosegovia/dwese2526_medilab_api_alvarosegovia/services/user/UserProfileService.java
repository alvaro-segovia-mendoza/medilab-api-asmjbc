package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfileDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfilePatchDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {

    UserProfileDTO getFormByEmail(String email);

    UserProfileDTO getFormById(Long userId);

    void updateProfile(String email, UserProfilePatchDTO profileDTO, MultipartFile profileImageFile);
}
