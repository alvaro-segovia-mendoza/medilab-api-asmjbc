package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.communication;

import java.util.Map;

/**
 * Contrato para construir URLs publicas de la aplicacion y del frontend.
 */
public interface AppUrlService {
    /**
     * Construye la URL publica del formulario de reset a partir de un token opaco.
     *
     * @param rawToken token de recuperacion en claro.
     * @return URL completa para el frontend.
     */
    String buildResetUrl(String rawToken);

    /**
     * Construye una URL publica a partir de una ruta y sus parametros.
     *
     * @param path ruta relativa o absoluta a anexar.
     * @param queryParams parametros de query opcionales.
     * @return URL resultante.
     */
    String buildUrl(String path, Map<String, String> queryParams);
}
