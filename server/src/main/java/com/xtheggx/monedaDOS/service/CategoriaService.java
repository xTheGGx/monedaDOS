package com.xtheggx.monedaDOS.service;

import com.xtheggx.monedaDOS.dto.CategoriaDTO;
import com.xtheggx.monedaDOS.model.Categoria;

import java.util.List;

public interface CategoriaService {
    List<Categoria> listarCategoriasUsuario(Long userId);

    Categoria crearCategoria(Long userId, CategoriaDTO dto);

    Categoria actualizarCategoria(Long categoriaId, Long userId, CategoriaDTO dto);

    void eliminarCategoria(Long categoriaId, Long userId);
}
