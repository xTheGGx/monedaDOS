package com.xtheggx.monedaDOS.controller.mvc;

import com.xtheggx.monedaDOS.auth.AuthUtils;
import com.xtheggx.monedaDOS.dto.TransaccionDTO;
import com.xtheggx.monedaDOS.model.Categoria;
import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.repository.CategoriaRepository;
import com.xtheggx.monedaDOS.repository.CuentaRepository;
import com.xtheggx.monedaDOS.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final CuentaRepository cuentaRepository;
    private final CategoriaRepository categoriaRepository;
    private final AuthUtils authUtils;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "ok", required = false) String ok) {
        
        Long uid = authUtils.currentUserId();
        log.debug("[GET /transacciones] userId={}", uid);

        // --- Cargar datos para el formulario ---
        // 1. Cuentas del usuario
        List<Cuenta> cuentas = cuentaRepository.findAllByUsuarioIdUsuario(uid);
        model.addAttribute("cuentas", cuentas);

        // 2. Categorías del usuario
        List<Categoria> categorias = categoriaRepository.findByUsuarioIdUsuario(uid);
        model.addAttribute("categorias", categorias);
        
        // 3. Objeto DTO para el formulario
        model.addAttribute("transaccionDTO", new TransaccionDTO());
        
        // 4. Separar categorías por tipo para el JS
        model.addAttribute("categoriasIngreso", categorias.stream()
                .filter(c -> c.getTipo().name().equals("INGRESO"))
                .collect(Collectors.toList()));
        model.addAttribute("categoriasEgreso", categorias.stream()
                .filter(c -> c.getTipo().name().equals("EGRESO"))
                .collect(Collectors.toList()));


        // --- Cargar datos de la página (transacciones existentes, filtros, etc.) ---
        // (Lógica de listado de transacciones existentes iría aquí)
        // model.addAttribute("transacciones", ...);

        // Mensajes flash
        if (error != null) model.addAttribute("error", error);
        if (ok != null) model.addAttribute("created", ok);
        
        return "home/transacciones";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("transaccionDTO") TransaccionDTO dto,
                        BindingResult br,
                        @RequestParam("tipoMovimiento") String tipoMovimiento,
                        RedirectAttributes ra) {

        Long uid = authUtils.currentUserId();
        log.info("[POST /transacciones] userId={}, payload={}, tipo={}", uid, dto, tipoMovimiento);

        // Asignar el ID de usuario al DTO
        dto.setUsuarioId(uid);

        // Validar errores de formulario
        if (br.hasErrors()) {
            br.getFieldErrors().forEach(fe -> log.warn(
                    "BindingError field={} rejectedValue={} message={}",
                    fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage()
            ));
            ra.addAttribute("error", "Datos de formulario inválidos");
            return "redirect:/transacciones";
        }
        
        // Ajustar el signo del monto según el tipo de movimiento seleccionado
        // Nos aseguramos que el monto sea positivo y luego aplicamos el signo.
        try {
            BigDecimal montoAbsoluto = dto.getMonto().abs();
            if ("EGRESO".equals(tipoMovimiento)) {
                dto.setMonto(montoAbsoluto.negate());
            } else {
                dto.setMonto(montoAbsoluto);
            }
        } catch (NullPointerException e) {
            log.warn("Monto nulo recibido");
            ra.addAttribute("error", "El monto no puede ser nulo");
            return "redirect:/transacciones";
        }

        // Intentar registrar la transacción
        try {
            transaccionService.registrarTransaccion(dto);
            ra.addAttribute("ok", "¡Transacción registrada!");
            
        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            // Captura errores del servicio (ej. signo incorrecto vs categoría)
            log.error("Error de lógica/datos al registrar transacción. dto={}", dto, ex);
            // El trigger de BD o la lógica de servicio pueden lanzar esto
            if (ex.getMessage().contains("debe ser negativo") || ex.getMessage().contains("debe ser positivo")) {
                 ra.addAttribute("error", "La categoría seleccionada no coincide con el tipo (Ingreso/Gasto)");
            } else {
                 ra.addAttribute("error", "Error al guardar. Verifique los datos.");
            }
        } catch (Exception ex) {
            log.error("Error inesperado al registrar transacción. dto={}", dto, ex);
            ra.addAttribute("error", "Error inesperado");
        }
        
        return "redirect:/transacciones";
    }
}