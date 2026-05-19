package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.Role;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.entities.User;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n.LocaleService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n.MessageService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerI18nMvcTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("view.user.flash.detailNotFound", Locale.of("es"), "No se encontró el usuario solicitado.");

        MessageService messageService = new MessageService(messageSource, new LocaleService());
        UserService userService = new MissingUserServiceStub();

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, messageService)).build();
    }

    @Test
    void shouldUseLocalizedFlashMessageWhenUserIsMissing() throws Exception {
        mockMvc.perform(get("/users/edit").param("id", "99").locale(Locale.of("es")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("errorMessage", "No se encontró el usuario solicitado."));
    }

    private static final class MissingUserServiceStub implements UserService {

        @Override
        public Page<UserDTO> list(Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Page<UserDetailDTO> listAllWithProfile(Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Page<UserDetailDTO> listByRole(String roleName, Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Page<UserDetailDTO> searchPatients(String query, Pageable pageable) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserUpdateDTO getForEdit(Long id) {
            throw new ResourceNotFoundException("user", "id", id);
        }

        @Override
        public Long create(UserCreateDTO dto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void update(UserUpdateDTO dto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserDetailDTO getDetail(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Role> listRoles() {
            throw new UnsupportedOperationException();
        }

        @Override
        public User registerPatient(String email, String rawPassword) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User getByEmail(String email) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User findOrCreateOAuth2User(String email, String displayName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updatePassword(Long userId, String rawPassword) {
            throw new UnsupportedOperationException();
        }
    }
}
