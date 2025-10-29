package com.xtheggx.monedados.repository;

import com.xtheggx.monedados.model.Categoria;
import com.xtheggx.monedados.model.CategoriaTipo;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoriaRepository extends JpaRepository <Categoria, Integer>{

    List<Categoria> findByTipo(CategoriaTipo tipo);

    List<Categoria> findByUsuarioIdUsuario(Integer usuarioIdUsuario);

    Categoria findByUsuarioIdUsuarioAndNombre(Integer usuarioIdUsuario, String nombre, Limit limit);

}
