package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.common.ApiErrorDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.AuthRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.AuthRefreshRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.AuthResponseDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.PasswordResetDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.PasswordResetRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.auth.RegisterRequestDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.auth.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para autenticacion, registro y recuperacion de credenciales.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login, registro y gestión de credenciales JWT")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Autentica a un usuario y devuelve su par de tokens JWT.
     *
     * @param authRequest credenciales de acceso.
     * @return tokens y metadatos de autenticacion.
     * @throws AuthenticationException si las credenciales no son validas.
     */
    @PostMapping("/authenticate")
    @Operation(summary = "Autenticar usuario", description = "Devuelve un JWT válido para consumir la API.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación correcta"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "CredencialesInvalidas",
                                    value = """
                                            {
                                              "timestamp": "2026-04-13T10:00:00Z",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "code": "AUTH_INVALID_CREDENTIALS",
                                              "message": "El correo electrónico o la contraseña no coinciden",
                                              "path": "/api/auth/authenticate",
                                              "resource": null,
                                              "field": null,
                                              "value": null,
                                              "fieldErrors": {
                                                "email": "El correo electrónico o la contraseña no son válidos",
                                                "password": "El correo electrónico o la contraseña no son válidos"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirements
    public ResponseEntity<AuthResponseDTO> authenticate(@Valid @RequestBody AuthRequestDTO authRequest) throws AuthenticationException {
        logger.info("Procesando autenticacion para email={}", authRequest.getEmail());
        return ResponseEntity.ok(authenticationService.authenticate(authRequest));
    }

    /**
     * Registra un nuevo paciente y devuelve sus tokens iniciales.
     *
     * @param request datos de registro del paciente.
     * @return respuesta 201 con el resultado de autenticacion inicial.
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar paciente", description = "Crea un nuevo usuario paciente y devuelve access y refresh token.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro correcto"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirements
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        logger.info("Registrando nuevo paciente con email={}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(request));
    }

    /**
     * Emite un nuevo access token a partir de un refresh token valido.
     *
     * @param request peticion con el refresh token.
     * @return nueva respuesta de autenticacion.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refrescar access token", description = "Emite un nuevo access token usando un refresh token válido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refresh correcto"),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirements
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody AuthRefreshRequestDTO request) {
        logger.info("Procesando renovacion de access token");
        return ResponseEntity.ok(authenticationService.refresh(request));
    }

    /**
     * Solicita un enlace de recuperacion de contrasena para una cuenta existente.
     *
     * @param request email de la cuenta a recuperar.
     * @param httpRequest peticion original para resolver contexto del cliente.
     * @return respuesta vacia cuando la solicitud ha sido procesada.
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar reset de contraseña", description = "Genera un token de recuperación y envía el enlace por email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud procesada"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna cuenta con ese email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirements
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO request,
                                               HttpServletRequest httpRequest) {
        logger.info("Procesando solicitud de recuperacion de contrasena para email={}", request.getEmail());
        authenticationService.forgotPassword(request, httpRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * Restablece la contrasena de una cuenta a partir de un token opaco valido.
     *
     * @param request token de recuperacion y nueva contrasena.
     * @return respuesta vacia si la operacion finaliza correctamente.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña", description = "Consume un token de recuperación y actualiza la contraseña del usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
            @ApiResponse(responseCode = "400", description = "Token inválido, expirado o contraseñas no coinciden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)))
    })
    @SecurityRequirements
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetDTO request) {
        logger.info("Procesando reseteo de contrasena mediante token opaco");
        authenticationService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
