package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepcion de negocio para devolver errores funcionales controlados en la API.
 */
@Getter
public class ApiBusinessException extends RuntimeException {

    private final String code;
    private final String messageKey;
    private final Object[] args;
    private final HttpStatus status;
    private final String resource;
    private final String field;
    private final Object value;

    public ApiBusinessException(String code, String messageKey, HttpStatus status, Object... args) {
        this(code, messageKey, status, null, null, null, args);
    }

    public ApiBusinessException(String code, String messageKey, HttpStatus status,
                                String resource, String field, Object value, Object... args) {
        super(messageKey);
        this.code = code;
        this.messageKey = messageKey;
        this.args = args == null ? new Object[0] : args;
        this.status = status;
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    public static ApiBusinessException badRequest(String code, String messageKey, Object... args) {
        return new ApiBusinessException(code, messageKey, HttpStatus.BAD_REQUEST, args);
    }
}
