package com.xtheggx.monedaDOS.controller;

import com.xtheggx.monedaDOS.auth.AuthUtils;
import com.xtheggx.monedaDOS.dto.TransaccionDTO;
import com.xtheggx.monedaDOS.model.CategoriaTipo;
import com.xtheggx.monedaDOS.model.Transaccion;
import com.xtheggx.monedaDOS.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final AuthUtils authUtils;

    @GetMapping
    public ResponseEntity<Page<Transaccion>> listar(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Integer cuentaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = authUtils.currentUserId();
        
        Page<Transaccion> resultado;

        // Caso 1: Filtrado complejo 
        if (categoriaId != null || cuentaId != null || (tipo != null && !tipo.isEmpty())) {
            LocalDateTime from = null;
            LocalDateTime to = null;
            
            if (mes != null) {
                from = mes.atDay(1).atStartOfDay();
                to = mes.atEndOfMonth().atTime(23, 59, 59);
            }

            // Traer todas (o por fecha) y filtrar en memoria
            List<Transaccion> todas = (mes != null) 
                    ? transaccionService.listarTransaccionesPorUsuarioYFechas(userId, from, to)
                    : transaccionService.listarTransaccionesPorUsuario(userId);

            // Aplicar filtros stream
            if (tipo != null && !tipo.isEmpty()) {
                CategoriaTipo tipoEnum = CategoriaTipo.valueOf(tipo);
                todas = todas.stream().filter(t -> t.getCategoria().getTipo() == tipoEnum).collect(Collectors.toList());
            }
            if (categoriaId != null) {
                todas = todas.stream().filter(t -> t.getCategoria().getIdCategoria().equals(categoriaId)).collect(Collectors.toList());
            }
            if (cuentaId != null) {
                todas = todas.stream().filter(t -> t.getCuenta().getIdCuenta().equals(cuentaId)).collect(Collectors.toList());
            }

            // Paginación manual sobre la lista filtrada
            int total = todas.size();
            int start = Math.min((int) PageRequest.of(page, size).getOffset(), total);
            int end = Math.min((start + size), total);
            
            resultado = new PageImpl<>(todas.subList(start, end), PageRequest.of(page, size), total);

        } else {
            // Caso 2: Filtrado simple (solo fecha o todo) usando consultas paginadas nativas
            if (mes != null) {
                LocalDateTime from = mes.atDay(1).atStartOfDay();
                LocalDateTime to = mes.atEndOfMonth().atTime(23, 59, 59);
                resultado = transaccionService.listarTransaccionesPaginadasPorFecha(userId, from, to, page, size);
            } else {
                resultado = transaccionService.listarTransaccionesPaginadas(userId, page, size);
            }
        }

        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody TransaccionDTO dto) {
        Long userId = authUtils.currentUserId();
        dto.setUsuarioId(userId); // Asegurar que el ID viene del token
        transaccionService.registrarTransaccion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Transacción creada"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody TransaccionDTO dto) {
        Long userId = authUtils.currentUserId();
        dto.setUsuarioId(userId);
        dto.setIdTransaccion(id); // Asegurar ID
        transaccionService.actualizarTransaccion(id, dto);
        return ResponseEntity.ok(Map.of("mensaje", "Transacción actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        transaccionService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Transacción eliminada"));
    }
}