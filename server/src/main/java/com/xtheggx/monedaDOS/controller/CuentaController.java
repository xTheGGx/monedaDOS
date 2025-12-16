package com.xtheggx.monedaDOS.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.xtheggx.monedaDOS.dto.CuentaResponse;
import com.xtheggx.monedaDOS.dto.UpdateCuentaDTO;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<CuentaResponse>> listar() {
        Long userId = authUtils.currentUserId();
        List<CuentaResponse> cuentas = cuentaService.listar(userId);
        return ResponseEntity.ok(cuentas);
    }

    // 2. Obtener saldo total de todas las cuentas del usuario
    @GetMapping("/saldo-total")
    public ResponseEntity<Map<String, BigDecimal>> obtenerSaldoTotal() {
        Long userId = authUtils.currentUserId();
        BigDecimal total = cuentaService.saldoTotal(userId);
        return ResponseEntity.ok(Map.of("saldoTotal", total));
    }

    // 3. Crear nueva cuenta
    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaDTO dto) {
        Long userId = authUtils.currentUserId();
        CuentaResponse creada = cuentaService.crear(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // 4. Actualizar cuenta (nombre, divisa, tipo...)
    @PutMapping
    public ResponseEntity<CuentaResponse> actualizarCuenta(
            @PathVariable("id") Long idCuenta,
            @Valid @RequestBody UpdateCuentaDTO dto
            ){
        Long userId = authUtils.currentUserId();
        CuentaResponse actualizada = cuentaService.actualizar(idCuenta, userId, dto);
        return ResponseEntity.ok(actualizada);
    }
    // 5. Eliminar cuenta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable("id") Long idCuenta
    ){
        Long userId = authUtils.currentUserId();
        cuentaService.eliminar(idCuenta, userId);
        return ResponseEntity.noContent().build();
    }

}
