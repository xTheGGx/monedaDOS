package com.xtheggx.monedaDOS.service.impl;

import com.xtheggx.monedaDOS.dto.CategoriaDTO;
import com.xtheggx.monedaDOS.exception.ResourceNotFoundException;
import com.xtheggx.monedaDOS.model.Categoria;
import com.xtheggx.monedaDOS.model.CategoriaTipo;
import com.xtheggx.monedaDOS.model.Usuario;
import com.xtheggx.monedaDOS.repository.CategoriaRepository;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import com.xtheggx.monedaDOS.repository.TransaccionRepository;
import com.xtheggx.monedaDOS.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepo;
    private final UsuarioRepository usuarioRepo;
    private final TransaccionRepository transaccionRepo;

    @Override
    public List<Categoria> listarCategoriasUsuario(Long userId) {
        // Obtener categorías globales y del usuario
        List<Categoria> globales = categoriaRepo.findByUsuarioIsNull();
        List<Categoria> usuarioCats = categoriaRepo.findByUsuarioIdUsuario(userId.longValue());
        // Combinar y ordenar por nombre (ignorando mayúsculas/minúsculas para consistencia)
        List<Categoria> todas = globales;
        todas.addAll(usuarioCats);
        todas = todas.stream()
                .sorted((c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()))
                .collect(Collectors.toList());
        return todas;
    }

    @Override
    public Categoria crearCategoria(Long userId, CategoriaDTO dto) {
        // Verificar usuario existente
        Usuario user = usuarioRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no existe"));
        // Crear y guardar nueva categoría
        Categoria nueva = new Categoria();
        nueva.setUsuario(user);
        nueva.setNombre(dto.getNombre());
        nueva.setTipo(dto.getTipo());
        nueva.setClasificacion(dto.getClasificacion());
        nueva.setColor(dto.getColor());
        try {
            return categoriaRepo.save(nueva);
        } catch (DataIntegrityViolationException e) {
            // Esto puede ocurrir si ya existe categoría con mismo nombre/tipo para el usuario
            throw new IllegalArgumentException("Ya existe una categoría '" + dto.getNombre() + "' en "
                    + (dto.getTipo() == CategoriaTipo.INGRESO ? "ingresos" : "gastos"));
        }
    }

    @Override
    public Categoria actualizarCategoria(Long categoriaId, Long userId, CategoriaDTO dto) {
        Categoria categoria = categoriaRepo.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría con id " + categoriaId + " no encontrada"));
        // Solo permitir editar si pertenece al usuario (o global? Global no editable por usuario)
        if (categoria.getUsuario() == null || !categoria.getUsuario().getIdUsuario().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para editar esta categoría");
        }
        categoria.setNombre(dto.getNombre());
        categoria.setTipo(dto.getTipo());
        categoria.setClasificacion(dto.getClasificacion());
        categoria.setColor(dto.getColor());
        try {
            return categoriaRepo.save(categoria);
        } catch (DataIntegrityViolationException e) {
            // Nombre duplicado (mismo usuario y tipo)
            throw new IllegalArgumentException("Ya existe una categoría '" + dto.getNombre() + "' en "
                    + (dto.getTipo() == CategoriaTipo.INGRESO ? "ingresos" : "gastos"));
        }
    }

    @Override
    public void eliminarCategoria(Long categoriaId, Long userId) {
        Categoria categoria = categoriaRepo.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría con id " + categoriaId + " no encontrada"));
        if (categoria.getUsuario() == null || !categoria.getUsuario().getIdUsuario().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta categoría");
        }
        // Verificar si tiene transacciones asociadas
        long countTx = transaccionRepo.count();  // alternativa: método específico para contar transacciones por categoria
        // *** Si es necesario, implementar TransaccionRepository.countByCategoriaIdCategoria(categoriaId) para eficiencia ***
        // Aquí por simplicidad se verifica manualmente:
        if (countTx > 0 && transaccionRepo.findByCategoriaTipoAndCategoriaClasificacion(
                        categoria.getTipo(), categoria.getClasificacion()).stream()
                .anyMatch(tx -> tx.getCategoria().getIdCategoria().equals(categoriaId))) {
            throw new IllegalArgumentException("No se puede eliminar la categoría porque tiene transacciones registradas");
        }
        categoriaRepo.delete(categoria);
    }
}
