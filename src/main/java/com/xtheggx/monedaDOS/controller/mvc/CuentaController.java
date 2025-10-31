package com.xtheggx.monedaDOS.controller.mvc;

import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.model.CuentaTipo;
import com.xtheggx.monedaDOS.service.CuentaService;
import com.xtheggx.monedaDOS.auth.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    private final AuthUtils authUtils;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "ok", required = false) String ok) {
        Long uid = authUtils.currentUserId();
        log.debug("[GET /cuentas] userId={}", uid);

        model.addAttribute("cuentas", cuentaService.listar(uid));
        model.addAttribute("saldoTotal", cuentaService.saldoTotal(uid));
        model.addAttribute("cuentaDTO", new CuentaDTO());  // 👈 coincide con tu HTML
        model.addAttribute("tipos", CuentaTipo.values());

        if (error != null) model.addAttribute("error", error);
        if (ok != null) model.addAttribute("created", ok);
        return "home/cuentas";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("cuentaDTO") CuentaDTO dto,
                        BindingResult br,
                        RedirectAttributes ra) {

        log.info("[POST /cuentas] payload={}", dto);

        // Log detallado de errores de binding/validación
        if (br.hasErrors()) {
            br.getFieldErrors().forEach(fe -> log.warn(
                    "BindingError field={} rejectedValue={} message={}",
                    fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage()
            ));
            ra.addAttribute("error", "Datos inválidos");
            return "redirect:/cuentas";
        }

        try {
            Long uid = authUtils.currentUserId();
            log.debug("Creando cuenta para userId={}", uid);
            cuentaService.crear(uid, dto);
            ra.addAttribute("ok", "¡Cuenta creada!");
        } catch (DataIntegrityViolationException ex) {
            log.error("Constraint/duplicado al crear cuenta. dto={}", dto, ex);
            ra.addAttribute("error", "No se pudo crear la cuenta (duplicado o constraint)");
        } catch (Exception ex) {
            log.error("Error inesperado al crear cuenta. dto={}", dto, ex);
            ra.addAttribute("error", "Error inesperado");
        }
        return "redirect:/cuentas";
    }
}
