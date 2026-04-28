package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user;


import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Role;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.UserProfile;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.mappers.UserMapper;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RoleRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.RutaRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserProfileRepository;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<UserDTO> list(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailDTO> listAllWithProfile(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toDetailDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailDTO> listByRole(String roleName, Pageable pageable) {
        return userRepository.findDistinctByRolesName(roleName, pageable)
                .map(UserMapper::toDetailDTO);
    }

    @Override
    public UserUpdateDTO getForEdit(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("user", "id", id)
                );

        return UserMapper.toUpdateDTO(user);
    }

    @Override
    public Long create(UserCreateDTO dto) {
        String normalizedEmail = normalizeEmail(dto.getEmail());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException(
                    "user", "email", normalizedEmail
            );
        }

        dto.setEmail(normalizedEmail);

        User user = UserMapper.toEntity(dto);

        // El alta administrativa no envía contraseña; generamos una temporal
        // y aplicamos defaults de servidor en campos obligatorios.
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setFailedLoginAttempts(dto.getFailedLoginAttempts() != null ? dto.getFailedLoginAttempts() : 0);

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findByIdIn(dto.getRoleIds()));
            user.setRoles(roles);
        }

        return userRepository.save(user).getId();
    }


    @Override
    public void update(UserUpdateDTO dto) {
        String normalizedEmail = normalizeEmail(dto.getEmail());

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, dto.getId())) {
            throw new DuplicateResourceException(
                    "user", "email", normalizedEmail
            );
        }

        dto.setEmail(normalizedEmail);

        User user = userRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "user", "id", dto.getId()
                        )
                );

        UserMapper.copyToExistingEntity(dto, user);

        if (dto.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(
                    roleRepository.findByIdIn(dto.getRoleIds())
            );
            user.setRoles(roles);
        }

        userRepository.save(user);
    }


    @Override
    public void delete(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            userRepository.findByEmailIgnoreCase(auth.getName())
                    .filter(current -> current.getId().equals(id))
                    .ifPresent(u -> { throw ApiBusinessException.badRequest("USER_CANNOT_DELETE_OWN_ACCOUNT", "api.error.user.cannotDeleteOwnAccount"); });
        }
        if (rutaRepository.existsByTecnicosId(id)) {
            throw ApiBusinessException.badRequest("USER_HAS_RUTAS_ASSIGNED", "api.error.user.hasRutasAssigned");
        }

        User user = userRepository.findWithRolesAndProfileById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));

        UserProfile profile = user.getProfile();
        if (profile != null) {
            // Rompemos ambos lados antes del delete para evitar que Hibernate
            // intente resincronizar el UserProfile contra un User ya eliminado.
            profile.setUser(null);
            user.setProfile(null);
            userProfileRepository.delete(profile);
        }

        userRepository.delete(user);
    }

    @Override
    public UserDetailDTO getDetail(Long id) {

        User user = userRepository.findByIdWithProfile(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("user", "id", id)
                );

        return UserMapper.toDetailDTO(user);
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public User registerPatient(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException("user", "email", normalizedEmail);
        }

        Role patientRole = roleRepository.findByName("ROLE_PACIENTE")
                .orElseThrow(() -> new ResourceNotFoundException("role", "name", "ROLE_PACIENTE"));

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setActive(true);
        user.setAccountNonLocked(true);
        user.setFailedLoginAttempts(0);
        user.setEmailVerified(false);
        user.setMustChangePassword(false);
        user.setLastPasswordChange(LocalDateTime.now());
        user.setRoles(Set.of(patientRole));
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user", "email", normalizedEmail));
    }

    @Override
    public void updatePassword(Long userId, String rawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", userId));

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setLastPasswordChange(LocalDateTime.now());
        user.setMustChangePassword(false);
        userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
