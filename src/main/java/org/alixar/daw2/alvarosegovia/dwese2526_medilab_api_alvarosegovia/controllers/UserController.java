package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.controllers;


import jakarta.validation.Valid;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserCreateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserDetailDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.dto.user.UserUpdateDTO;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.DuplicateResourceException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.exceptions.ResourceNotFoundException;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.i18n.MessageService;
import org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * Controlador que maneja las operaciones CRUD para la entidad 'User'.
 * Se apoya exclusivamente en {@link UserService}.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final long PASSWORD_EXPIRY_DAYS = 90;

    private final UserService userService;
    private final MessageService messageService;

    public UserController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    /**
     * Muestra el formulario para crear un nuevo usuario.
     */
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("user", new UserCreateDTO());
        model.addAttribute("listRoles", userService.listRoles()); // roles para el select
        return "views/user/user-form";
    }

    /**
     * Muestra el formulario de edición de un usuario existente.
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            UserUpdateDTO userDTO = userService.getForEdit(id);
            model.addAttribute("user", userDTO);
            model.addAttribute("listRoles", userService.listRoles());
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario con ID {}", id);
            addFlashError(redirectAttributes, "view.user.flash.detailNotFound");
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Error al cargar el usuario {}: {}", id, e.getMessage(), e);
            addFlashError(redirectAttributes, "view.user.flash.editError");
            return "redirect:/users";
        }
        return "views/user/user-form";
    }

    /**
     * Muestra el detalle de un usuario.
     */
    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            UserDetailDTO userDTO = userService.getDetail(id);
            model.addAttribute("user", userDTO);
            return "views/user/user-detail";
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró el usuario con ID {}", id);
            addFlashError(redirectAttributes, "view.user.flash.detailNotFound");
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle del usuario {}: {}", id, e.getMessage(), e);
            addFlashError(redirectAttributes, "view.user.flash.detailError");
            return "redirect:/users";
        }
    }

    /**
     * Lista todos los usuarios paginados.
     */
    @GetMapping
    public String listUsers(@PageableDefault(size = 10, sort = "email", direction = Sort.Direction.ASC) Pageable pageable,
                            Model model) {
        try {
            Page<UserDTO> users = userService.list(pageable);
            model.addAttribute("page", users);

            String sortParam = "email,asc";
            if (users.getSort().isSorted()) {
                Sort.Order order = users.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar los usuarios: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", messageService.getMessage("view.user.flash.listError"));
        }
        return "views/user/user-list";
    }

    /**
     * Inserta un nuevo usuario.
     */
    @PostMapping("/insert")
    public String insertUser(@Valid @ModelAttribute("user") UserCreateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "views/user/user-form";
        }

        try {
            userService.create(userDTO);
            logger.info("Usuario {} insertado con éxito.", userDTO.getEmail());
            return "redirect:/users";
        } catch (DuplicateResourceException ex) {
            logger.warn("Email duplicado: {}", userDTO.getEmail());
            addFlashError(redirectAttributes, "view.user.flash.insertDuplicateEmail");
            return "redirect:/users/new";
        } catch (Exception e) {
            logger.error("Error al insertar el usuario {}: {}", userDTO.getEmail(), e.getMessage(), e);
            addFlashError(redirectAttributes, "view.user.flash.insertError");
            return "redirect:/users/new";
        }
    }

    /**
     * Actualiza un usuario existente.
     */
    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UserUpdateDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "views/user/user-form";
        }

        try {
            userService.update(userDTO);
            logger.info("Usuario con ID {} actualizado con éxito.", userDTO.getId());
            return "redirect:/users";
        } catch (DuplicateResourceException ex) {
            logger.warn("Email duplicado: {}", userDTO.getEmail());
            addFlashError(redirectAttributes, "view.user.flash.updateDuplicateEmail");
            return "redirect:/users/edit?id=" + userDTO.getId();
        } catch (ResourceNotFoundException ex) {
            addFlashError(redirectAttributes, "view.user.flash.detailNotFound");
            return "redirect:/users";
        } catch (Exception e) {
            addFlashError(redirectAttributes, "view.user.flash.updateError");
            return "redirect:/users/edit?id=" + userDTO.getId();
        }
    }

    /**
     * Elimina un usuario.
     */
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            logger.info("Usuario con ID {} eliminado con éxito.", id);
        } catch (ResourceNotFoundException ex) {
            addFlashError(redirectAttributes, "view.user.flash.detailNotFound");
        } catch (Exception e) {
            addFlashError(redirectAttributes, "view.user.flash.deleteError");
        }
        return "redirect:/users";
    }

    private void addFlashError(RedirectAttributes redirectAttributes, String messageKey) {
        redirectAttributes.addFlashAttribute("errorMessage", messageService.getMessage(messageKey));
    }
}
