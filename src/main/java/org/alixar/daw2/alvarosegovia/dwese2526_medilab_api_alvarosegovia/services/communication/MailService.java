package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.communication;

import java.util.Locale;
import java.util.Map;

/**
 * Contrato para el envio de correos transaccionales de la aplicacion.
 */
public interface MailService {
    /**
     * Envia un correo en texto plano.
     *
     * @param to destinatario.
     * @param subject asunto del mensaje.
     * @param text cuerpo en texto plano.
     */
    void sendText(String to, String subject, String text);

    /**
     * Envia un correo en formato HTML.
     *
     * @param to destinatario.
     * @param subject asunto del mensaje.
     * @param html cuerpo HTML.
     */
    void sendHtml(String to, String subject, String html);

    /**
     * Envia un correo renderizado desde una plantilla internacionalizada.
     *
     * @param to destinatario.
     * @param subjectKey clave i18n del asunto.
     * @param templateName nombre del template.
     * @param variables variables del template.
     * @param locale locale de renderizado.
     */
    void sendTemplate(String to,
                      String subjectKey,
                      String templateName,
                      Map<String, Object> variables,
                      Locale locale);
}
