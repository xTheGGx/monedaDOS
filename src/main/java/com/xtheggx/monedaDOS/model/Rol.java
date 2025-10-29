package com.xtheggx.monedados.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROL")
@Data @NoArgsConstructor @AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    // Nombre del rol (e.g., "ADMIN", "USER")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    // Descripción del rol
    @Column(length = 255)
    private String descripcion;
}

