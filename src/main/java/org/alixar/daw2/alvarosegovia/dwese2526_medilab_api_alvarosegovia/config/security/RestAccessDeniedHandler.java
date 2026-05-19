package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.common.ApiErrorDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ApiErrorFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Manejador REST para accesos denegados por falta de permisos.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiErrorFactory apiErrorFactory;
    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ApiErrorFactory apiErrorFactory, ObjectMapper objectMapper) {
        this.apiErrorFactory = apiErrorFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        writeApiError(response, apiErrorFactory.accessDenied(request));
    }

    private void writeApiError(HttpServletResponse response, ApiErrorDTO body) throws IOException {
        response.setStatus(body.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
