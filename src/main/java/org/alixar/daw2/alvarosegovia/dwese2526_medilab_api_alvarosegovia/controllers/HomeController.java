package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC que resuelve la pagina de inicio de la aplicacion.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Renderiza la pagina de inicio.
     *
     * @param model modelo MVC de la vista.
     * @return nombre de la plantilla principal.
     */
    @GetMapping("/")
    public String home(Model model) {
        logger.info("Renderizando pagina de inicio");
        return "index";
    }
}
