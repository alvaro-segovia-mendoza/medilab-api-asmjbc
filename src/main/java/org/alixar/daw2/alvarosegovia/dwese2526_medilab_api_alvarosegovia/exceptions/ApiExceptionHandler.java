package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.common.ApiErrorDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API.
 * Convierte excepciones de negocio/validación en respuestas HTTP con un ApiErrorDTO homogéneo.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private final ApiErrorFactory apiErrorFactory;

    public ApiExceptionHandler(ApiErrorFactory apiErrorFactory) {
        this.apiErrorFactory = apiErrorFactory;
    }


    /**
     * Recurso no encontrado -> 404 Not Found.
     */
    @ExceptionHandler(ApiBusinessException.class)
    public ResponseEntity<ApiErrorDTO> handleBusiness(ApiBusinessException ex, HttpServletRequest req) {
        return ResponseEntity.status(ex.getStatus()).body(apiErrorFactory.fromBusinessException(ex, req));
    }

    /**
     * Ruta no existente -> 404 Not Found.
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiErrorDTO> handleMissingRoute(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiErrorFactory.basic(HttpStatus.NOT_FOUND, "ROUTE_NOT_FOUND", "api.error.routeNotFound", req));
    }


    /**
     * Errores de validación de @Valid -> 400 Bad Request con errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorFactory.validation(req, fieldErrors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorFactory.constraintViolation(req, ex));
    }


    /**
     * JSON mal formado o tipos incompatibles -> 400 Bad Request.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiErrorFactory.basic(HttpStatus.BAD_REQUEST, "INVALID_JSON", "api.error.invalidJson", req));
    }

    /**
     * IllegalArgumentException residual -> 400 genérico.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiErrorFactory.basic(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "api.error.invalidRequest", req));
    }

    /**
     * Restricciones de integridad de datos no capturadas previamente -> 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                    HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiErrorFactory.basic(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", "api.error.dataIntegrity", req));
    }


    /**
     * Cualquier error no controlado -> 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneric(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiErrorFactory.basic(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "api.error.internal", req));
    }

    /**
     * Credenciales inválidas / fallo de autenticación -> 401 Unauthorized.
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiErrorDTO> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorFactory.invalidCredentials(req));
    }


    /**
     * Acceso denegado -> 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiErrorFactory.accessDenied(req));
    }
}
