package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.common.ApiErrorDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Factoría utilitaria para construir respuestas de error consistentes para la API.
 */
@Component
public class ApiErrorFactory {

    private final MessageService messageService;

    public ApiErrorFactory(MessageService messageService) {
        this.messageService = messageService;
    }

    public ApiErrorDTO fromBusinessException(ApiBusinessException ex, HttpServletRequest request) {
        return new ApiErrorDTO(
                Instant.now(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getCode(),
                messageService.getMessageForRequest(request, ex.getMessageKey(), ex.getArgs()),
                request.getRequestURI(),
                ex.getResource(),
                ex.getField(),
                ex.getValue(),
                null
        );
    }

    public ApiErrorDTO basic(HttpStatus status, String code, String messageKey, HttpServletRequest request, Object... args) {
        return new ApiErrorDTO(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                messageService.getMessageForRequest(request, messageKey, args),
                request.getRequestURI(),
                null,
                null,
                null,
                null
        );
    }

    public ApiErrorDTO validation(HttpServletRequest request, Map<String, String> fieldErrors) {
        return new ApiErrorDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                messageService.getMessageForRequest(request, "api.error.validationFailed"),
                request.getRequestURI(),
                null,
                null,
                null,
                fieldErrors
        );
    }

    public ApiErrorDTO constraintViolation(HttpServletRequest request, ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath() == null ? "request" : violation.getPropertyPath().toString();
            int lastDot = propertyPath.lastIndexOf('.');
            String field = lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
            fieldErrors.put(field, violation.getMessage());
        }
        return validation(request, fieldErrors);
    }

    public ApiErrorDTO invalidCredentials(HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String invalidCredentials = messageService.getMessageForRequest(request, "security.authentication.invalid.field");
        fieldErrors.put("email", invalidCredentials);
        fieldErrors.put("password", invalidCredentials);

        return new ApiErrorDTO(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "AUTH_INVALID_CREDENTIALS",
                messageService.getMessageForRequest(request, "security.authentication.invalid"),
                request.getRequestURI(),
                null,
                null,
                null,
                fieldErrors
        );
    }

    public ApiErrorDTO unauthorized(HttpServletRequest request) {
        return basic(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED", "security.authentication.required", request);
    }

    public ApiErrorDTO accessDenied(HttpServletRequest request) {
        return basic(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "security.accessDenied", request);
    }
}
