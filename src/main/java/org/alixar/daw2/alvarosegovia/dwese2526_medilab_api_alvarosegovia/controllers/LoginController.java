package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC que prepara la vista de inicio de sesion.
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    /**
     * Maneja las solicitudes GET a la página de inicio de sesión.
     * Recupera mensajes de error de la sesión, si existen, y los pasa al modelo
     * para ser mostrados en la vista de login.
     *
     * @param request El objeto {@link HttpServletRequest} que contiene la solicitud HTTP.
     * @param model   El objeto {@link Model} que se utiliza para pasar atributos a la vista.
     * @return El nombre de la plantilla de Thymeleaf que renderiza la página de login.
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        logger.info("Renderizando pagina de login");
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            logger.warn("Mostrando mensaje de error previo en login");
            model.addAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage");
        }
        return "views/login/login";
    }
}
