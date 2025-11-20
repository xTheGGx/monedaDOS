package com.xtheggx.monedaDOS.controller.mvc;

import com.xtheggx.monedaDOS.dto.CategoriaDTO;
import com.xtheggx.monedaDOS.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    // POST: crear nueva categoría
    @PostMapping
    public String crearCategoria(@Valid @ModelAttribute("categoriaDTO") CategoriaDTO categoriaDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttrs) {
        // Obtener usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof com.xtheggx.monedaDOS.model.Usuario usuario) {
                userId = usuario.getIdUsuario();
            }
        }
        if (userId == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            // Si hay errores de validación, reenviar los datos ingresados y mensajes de error mediante flash attributes
            redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.categoriaDTO", bindingResult);
            redirectAttrs.addFlashAttribute("categoriaDTO", categoriaDTO);
            redirectAttrs.addFlashAttribute("catError", "Por favor corrige los errores del formulario de categoría");
            return "redirect:/transacciones";
        }
        try {
            categoriaService.crearCategoria(userId, categoriaDTO);
            // Podríamos enviar un mensaje de éxito, aunque la UI no muestra explícitamente confirmación de categoría creada
        } catch (Exception e) {
            // Manejar nombre duplicado u otros errores de negocio
            redirectAttrs.addFlashAttribute("categoriaDTO", categoriaDTO);
            redirectAttrs.addFlashAttribute("catError", e.getMessage());
        }
        return "redirect:/transacciones";
    }

    // POST: actualizar o eliminar categoría existente
    @PostMapping("/{id}")
    public String editarOEliminarCategoria(@PathVariable("id") Integer categoriaId,
                                           @Valid @ModelAttribute("categoriaDTO") CategoriaDTO categoriaDTO,
                                           BindingResult bindingResult,
                                           @RequestParam(value = "delete", required = false) String deleteFlag,
                                           RedirectAttributes redirectAttrs) {
        // Obtener usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof com.xtheggx.monedaDOS.model.Usuario usuario) {
                userId = usuario.getIdUsuario();
            }
        }
        if (userId == null) {
            return "redirect:/login";
        }
        if (deleteFlag != null) {
            // Solicitud de eliminación
            try {
                categoriaService.eliminarCategoria(categoriaId, userId);
                // Se podría añadir mensaje de éxito
            } catch (Exception e) {
                redirectAttrs.addFlashAttribute("catError", e.getMessage());
                // Mantener datos de categoría en el formulario si la eliminación falla (p.ej., por transacciones existentes)
                redirectAttrs.addFlashAttribute("categoriaDTO", categoriaDTO);
            }
            return "redirect:/transacciones";
        }
        // Si no es eliminación, es edición
        if (bindingResult.hasErrors()) {
            redirectAttrs.addFlashAttribute("org.springframework.validation.BindingResult.categoriaDTO", bindingResult);
            redirectAttrs.addFlashAttribute("categoriaDTO", categoriaDTO);
            redirectAttrs.addFlashAttribute("catError", "Por favor corrige los errores del formulario de categoría");
            return "redirect:/transacciones";
        }
        try {
            categoriaService.actualizarCategoria(categoriaId, userId, categoriaDTO);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("categoriaDTO", categoriaDTO);
            redirectAttrs.addFlashAttribute("catError", e.getMessage());
        }
        return "redirect:/transacciones";
    }
}
