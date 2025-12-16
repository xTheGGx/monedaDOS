package com.xtheggx.monedaDOS.controller;

import com.xtheggx.monedaDOS.auth.AuthUtils;
import com.xtheggx.monedaDOS.dto.DivisaDTO;
import com.xtheggx.monedaDOS.model.Divisa;
import com.xtheggx.monedaDOS.service.DivisaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/divisas")
@RequiredArgsConstructor
public class DivisaController {
    // Inyeccion de dependencias
    private final AuthUtils authUtils;
    private final DivisaService divisaService;

    @GetMapping
    public ResponseEntity<List<DivisaDTO>> listarDivisa() {
        Long userId = authUtils.currentUserId();
        List<Divisa> divisas = divisaService.listarDivisas(userId);
        var resp = divisas.stream()
                .map(d -> new DivisaDTO(d.getIdDivisa(), d.getCodigoDivisa(),d.getNombreDivisa()))
                .toList();
        return ResponseEntity.ok(resp);
    }

}
