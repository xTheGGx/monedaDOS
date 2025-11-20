package com.xtheggx.monedaDOS.controller;

import com.xtheggx.monedaDOS.auth.AuthUtils;
import com.xtheggx.monedaDOS.dto.CategoriaDTO;
import com.xtheggx.monedaDOS.model.Categoria;
import com.xtheggx.monedaDOS.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final AuthUtils authUtils;

    // 1. Listar categorías (Globales + Usuario)
    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        Long userId = authUtils.currentUserId();
        return ResponseEntity.ok(categoriaService.listarCategoriasUsuario(userId));
    }

    // 2. Crear categoría
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CategoriaDTO dto) {
        Long userId = authUtils.currentUserId();
        Categoria nueva = categoriaService.crearCategoria(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // 3. Actualizar categoría
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaDTO dto) {
        Long userId = authUtils.currentUserId();
        Categoria actualizada = categoriaService.actualizarCategoria(id, userId, dto);
        return ResponseEntity.ok(actualizada);
    }

    // 4. Eliminar categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        Long userId = authUtils.currentUserId();
        categoriaService.eliminarCategoria(id, userId);
        return ResponseEntity.ok(Map.of("mensaje", "Categoría eliminada"));
    }
}
