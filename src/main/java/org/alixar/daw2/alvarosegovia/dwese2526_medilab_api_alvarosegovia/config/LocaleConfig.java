package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.MessageSource;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n.LocaleService;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuracion de internacionalizacion y resolucion de locale para las peticiones web.
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    private final MessageSource messageSource;
    private final LocaleService localeService;

    public LocaleConfig(MessageSource messageSource, LocaleService localeService) {
        this.messageSource = messageSource;
        this.localeService = localeService;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(localeService.getDefaultLocale());
        resolver.setSupportedLocales(localeService.getSupportedLocales());
        return resolver;
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }

    @Override
    public Validator getValidator() {
        return localValidatorFactoryBean();
    }
}
