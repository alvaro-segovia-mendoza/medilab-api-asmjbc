package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Servicio de apoyo para resolver mensajes internacionalizados del backend.
 */
@Service
public class MessageService {

    private final MessageSource messageSource;
    private final LocaleService localeService;

    public MessageService(MessageSource messageSource, LocaleService localeService) {
        this.messageSource = messageSource;
        this.localeService = localeService;
    }

    public String getMessage(String key, Object... args) {
        return getMessageForLocale(key, localeService.resolve(LocaleContextHolder.getLocale()), args);
    }

    public String getMessageForRequest(HttpServletRequest request, String key, Object... args) {
        return getMessageForLocale(key, localeService.resolve(request), args);
    }

    public String getMessageForLocale(String key, Locale locale, Object... args) {
        Locale resolvedLocale = localeService.resolve(locale);
        try {
            return messageSource.getMessage(key, args, resolvedLocale);
        } catch (NoSuchMessageException ex) {
            Locale defaultLocale = localeService.getDefaultLocale();
            if (!resolvedLocale.equals(defaultLocale)) {
                try {
                    return messageSource.getMessage(key, args, defaultLocale);
                } catch (NoSuchMessageException ignored) {
                    return key;
                }
            }
            return key;
        }
    }
}
