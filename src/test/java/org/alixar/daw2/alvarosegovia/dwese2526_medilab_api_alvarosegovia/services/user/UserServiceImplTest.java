package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Role;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.UserProfile;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RoleRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserProfileRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void searchPatientsFiltersByProfileAndExcludesOtherRoles() {
        String searchToken = "PatientToken" + System.nanoTime();
        User patient = createUserWithRole("patient-search-" + System.nanoTime() + "@app.local", "ROLE_PACIENTE");
        createProfile(patient, searchToken, "Campos", "77889911T");
        createUserWithRole("doctor-search-" + System.nanoTime() + "@app.local", "ROLE_MEDICO");

        Page<UserDetailDTO> result = userService.searchPatients(searchToken, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(patient.getId(), result.getContent().getFirst().getId());
        assertTrue(result.getContent().getFirst().getRoles().contains("ROLE_PACIENTE"));
    }

    @Test
    void searchPatientsWithBlankQueryReturnsOnlyPatientsPaged() {
        User firstPatient = createUserWithRole("blank-patient-a-" + System.nanoTime() + "@app.local", "ROLE_PACIENTE");
        User secondPatient = createUserWithRole("blank-patient-b-" + System.nanoTime() + "@app.local", "ROLE_PACIENTE");
        createUserWithRole("blank-doctor-" + System.nanoTime() + "@app.local", "ROLE_MEDICO");

        Page<UserDetailDTO> result = userService.searchPatients("   ", PageRequest.of(0, 20));

        Set<Long> resultIds = result.getContent().stream()
                .map(UserDetailDTO::getId)
                .collect(java.util.stream.Collectors.toSet());
        assertTrue(resultIds.contains(firstPatient.getId()));
        assertTrue(resultIds.contains(secondPatient.getId()));
        assertTrue(result.getContent().stream()
                .allMatch(user -> user.getRoles().contains("ROLE_PACIENTE")));
    }

    private User createUserWithRole(String email, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName, roleName, roleName)));
        User user = new User(
                email,
                "$2a$12$k6ReF58EW2891dAvOYNaDeT9wwPMiG.se/8ZmESUObCXBbRCPrkVq",
                true,
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3),
                0,
                true,
                false
        );
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    private void createProfile(User user, String firstName, String lastName, String dni) {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setDni(dni);
        user.setProfile(profile);
        userProfileRepository.save(profile);
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
