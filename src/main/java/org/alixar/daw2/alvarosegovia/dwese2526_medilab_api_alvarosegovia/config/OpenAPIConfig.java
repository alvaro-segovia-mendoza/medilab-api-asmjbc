package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.common.ApiErrorDTO;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Medilab API",
                version = "1.0",
                description = "API para gestion de trailers, rutas, paradas operativas, slots reservables, citas, registros clinicos e historial medico"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenAPIConfig {

    /**
     * Añade respuestas 401 y 403 a todos los endpoints que requieren bearerAuth
     * (los que no tienen @SecurityRequirements vacío, es decir, todos excepto /auth/*).
     */
    @Bean
    public OperationCustomizer addSecurityResponses() {
        Schema<ApiErrorDTO> errorSchema = new Schema<ApiErrorDTO>().$ref("#/components/schemas/ApiErrorDTO");
        Content errorContent = new Content().addMediaType(
                org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                new MediaType().schema(errorSchema)
        );

        ApiResponse unauthorized = new ApiResponse()
                .description("Token ausente, inválido o expirado")
                .content(errorContent);
        ApiResponse forbidden = new ApiResponse()
                .description("Sin permisos para este recurso")
                .content(errorContent);

        return (operation, handlerMethod) -> {
            // Solo añadir a endpoints que tienen el esquema bearerAuth
            boolean requiresAuth = operation.getSecurity() == null ||
                    operation.getSecurity().stream()
                            .anyMatch(req -> req.containsKey("bearerAuth"));
            if (requiresAuth) {
                operation.getResponses().addApiResponse("401", unauthorized);
                operation.getResponses().addApiResponse("403", forbidden);
            }
            return operation;
        };
    }
}
