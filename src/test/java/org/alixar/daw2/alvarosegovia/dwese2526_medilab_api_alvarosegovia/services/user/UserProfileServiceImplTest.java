package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfileDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserProfilePatchDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.UserProfile;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserProfileRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserProfileServiceImplTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void updateProfileCreatesPartialProfileWhenOnlyLocaleIsProvided() {
        User user = userRepository.save(new User(
                "locale-only-" + System.nanoTime() + "@app.local",
                "$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq",
                true,
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3),
                0,
                true,
                false
        ));

        UserProfilePatchDTO patch = new UserProfilePatchDTO();
        patch.setLocale("en");

        userProfileService.updateProfile(user.getEmail(), patch, null);

        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals("en", profile.getLocale());
        assertNull(profile.getFirstName());
        assertNull(profile.getLastName());
        assertNull(profile.getDni());
        assertTrue(userProfileRepository.existsByUserId(user.getId()));
    }

    @Test
    void updateProfileStoresRelativeImagePathAndReturnsPublicApiUrl() {
        User user = userRepository.save(new User(
                "profile-image-" + System.nanoTime() + "@app.local",
                "$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq",
                true,
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3),
                0,
                true,
                false
        ));
        MockMultipartFile image = new MockMultipartFile(
                "profileImageFile",
                "avatar.png",
                "image/png",
                new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}
        );

        userProfileService.updateProfile(user.getEmail(), new UserProfilePatchDTO(), image);

        UserProfile persistedProfile = userProfileRepository.findByUserId(user.getId()).orElseThrow();
        assertTrue(persistedProfile.getProfileImage().startsWith("/uploads/"));

        UserProfileDTO dto = userProfileService.getFormByEmail(user.getEmail());
        assertTrue(dto.getProfileImage().startsWith("http://localhost:8080/uploads/"));
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean(name = "jwtKeyPair")
        @Primary
        KeyPair jwtKeyPair() throws Exception {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }
    }
}
