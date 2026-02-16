package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.UserProfileDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.UserProfilePatchDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {

    UserProfileDTO getFormByEmail(String email);

    void updateProfile(String email, UserProfilePatchDTO profileDTO, MultipartFile profileImageFile);
}
