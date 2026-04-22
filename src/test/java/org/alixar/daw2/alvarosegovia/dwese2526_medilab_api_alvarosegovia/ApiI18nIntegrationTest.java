package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiBusinessException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import jakarta.validation.constraints.Min;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
@Import(ApiI18nIntegrationTest.TestI18nController.class)
class ApiI18nIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldReturnValidationMessagesInSpanishByDefault() throws Exception {
        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validación fallida"))
                .andExpect(jsonPath("$.fieldErrors.email").value("El correo electrónico es obligatorio."))
                .andExpect(jsonPath("$.fieldErrors.password").value("La contraseña es obligatoria."));
    }

    @Test
    void shouldReturnValidationMessagesInEnglishWhenRequested() throws Exception {
        mockMvc.perform(post("/api/auth/authenticate")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email is required."))
                .andExpect(jsonPath("$.fieldErrors.password").value("Password is required."));
    }

    @Test
    void shouldFallbackToSpanishWhenLocaleIsUnsupported() throws Exception {
        mockMvc.perform(post("/api/auth/authenticate")
                        .header("Accept-Language", "fr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validación fallida"))
                .andExpect(jsonPath("$.fieldErrors.email").value("El correo electrónico es obligatorio."));
    }

    @Test
    void shouldReturnMalformedJsonMessageInEnglish() throws Exception {
        mockMvc.perform(post("/api/auth/authenticate")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_JSON"))
                .andExpect(jsonPath("$.message").value("Invalid or malformed JSON"));
    }

    @Test
    void shouldReturnUnauthorizedMessageInEnglish() throws Exception {
        mockMvc.perform(get("/api/rutas")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.message").value("You must authenticate to access this resource."));
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    void shouldReturnForbiddenMessageInEnglish() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnMissingRouteMessageInEnglish() throws Exception {
        mockMvc.perform(get("/api/does-not-exist")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROUTE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("The requested resource does not exist"));
    }

    @Test
    void shouldReturnUnauthorizedMessageInSpanish() throws Exception {
        mockMvc.perform(get("/api/rutas")
                        .header("Accept-Language", "es-ES"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.message").value("Debes autenticarte para acceder a este recurso."));
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    void shouldReturnForbiddenMessageInSpanish() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Accept-Language", "es-ES"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("No tienes permisos para acceder a este recurso."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleConstraintViolationInEnglish() throws Exception {
        mockMvc.perform(get("/api/test-i18n/constraint")
                        .header("Accept-Language", "en-US")
                        .param("value", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.value").value("Value must be greater than or equal to 5."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleResourceNotFoundInEnglish() throws Exception {
        mockMvc.perform(get("/api/test-i18n/not-found")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Resource user was not found (id=99)"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleDuplicateResourceInSpanish() throws Exception {
        mockMvc.perform(get("/api/test-i18n/duplicate")
                        .header("Accept-Language", "es-ES"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.message").value("Ya existe un recurso user con email=duplicado@medilab.es"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleDataIntegrityViolationInSpanish() throws Exception {
        mockMvc.perform(get("/api/test-i18n/data-integrity")
                        .header("Accept-Language", "es-ES"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DATA_INTEGRITY_VIOLATION"))
                .andExpect(jsonPath("$.message").value("Conflicto de integridad de datos: ya existe un valor que no puede repetirse."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleApiBusinessExceptionInEnglish() throws Exception {
        mockMvc.perform(get("/api/test-i18n/business")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TEST_BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Test business error."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFallbackToMessageKeyWhenTranslationIsMissing() throws Exception {
        mockMvc.perform(get("/api/test-i18n/missing-key")
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_TRANSLATION"))
                .andExpect(jsonPath("$.message").value("api.error.test.missing"));
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

    @RestController
    @Validated
    @RequestMapping("/api/test-i18n")
    static class TestI18nController {

        @GetMapping("/constraint")
        String constraint(@RequestParam @Min(value = 5, message = "{validation.test.min}") Integer value) {
            return Integer.toString(value);
        }

        @GetMapping("/not-found")
        String notFound() {
            throw new ResourceNotFoundException("user", "id", 99L);
        }

        @GetMapping("/duplicate")
        String duplicate() {
            throw new DuplicateResourceException("user", "email", "duplicado@medilab.es");
        }

        @GetMapping("/data-integrity")
        String dataIntegrity() {
            throw new DataIntegrityViolationException("duplicate key");
        }

        @GetMapping("/business")
        String business() {
            throw new ApiBusinessException("TEST_BUSINESS_ERROR", "api.error.test.business", HttpStatus.BAD_REQUEST);
        }

        @GetMapping("/missing-key")
        String missingKey() {
            throw new ApiBusinessException("MISSING_TRANSLATION", "api.error.test.missing", HttpStatus.BAD_REQUEST);
        }
    }
}
