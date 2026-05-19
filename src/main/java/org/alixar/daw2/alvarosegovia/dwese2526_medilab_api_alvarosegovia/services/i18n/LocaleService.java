package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Locale;

/**
 * Servicio de apoyo para resolver y normalizar el locale activo de la aplicacion.
 */
@Service
public class LocaleService {

    private static final Locale DEFAULT_LOCALE = Locale.of("es");
    private static final List<Locale> SUPPORTED_LOCALES = List.of(DEFAULT_LOCALE, Locale.ENGLISH);

    public Locale getDefaultLocale() {
        return DEFAULT_LOCALE;
    }

    public List<Locale> getSupportedLocales() {
        return SUPPORTED_LOCALES;
    }

    public Locale resolve(Locale locale) {
        if (locale == null) {
            return DEFAULT_LOCALE;
        }

        return switch (locale.getLanguage()) {
            case "en" -> Locale.ENGLISH;
            case "es" -> DEFAULT_LOCALE;
            default -> DEFAULT_LOCALE;
        };
    }

    public Locale resolve(HttpServletRequest request) {
        return request == null ? DEFAULT_LOCALE : resolve(RequestContextUtils.getLocale(request));
    }
}
