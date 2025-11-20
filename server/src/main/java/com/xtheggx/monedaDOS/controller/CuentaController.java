package com.xtheggx.monedaDOS.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xtheggx.monedaDOS.auth.AuthUtils;
import com.xtheggx.monedaDOS.dto.CuentaDTO;
import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.service.CuentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    private final AuthUtils authUtils;

    // 1. Listar cuentas por usuario
    @GetMapping
    public ResponseEntity<List<Cuenta>> listar() {
        Long userId = authUtils.currentUserId();
        return ResponseEntity.ok(cuentaService.listar(userId));
    }

    // 2. Obtener solo el saldo total
    @GetMapping("/saldo-total")
    public ResponseEntity<Map<String, BigDecimal>> obtenerSaldoTotal() {
        Long userId = authUtils.currentUserId();
        BigDecimal total = cuentaService.saldoTotal(userId);
        return ResponseEntity.ok(Map.of("saldoTotal", total));
    }

    // 3. Crear nueva cuenta
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CuentaDTO dto) {
        Long userId = authUtils.currentUserId();
        cuentaService.crear(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Cuenta creada exitosamente"));
    }

}
