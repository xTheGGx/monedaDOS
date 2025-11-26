package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ROL")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;
}

