package com.xtheggx.monedaDOS.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;


@Table(name = "USUARIO")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false, updatable = false)
    private Long idUsuario;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false)
    private String apellidoMaterno;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(name = "contrasena", nullable = false, length = 64)
    private String contrasena;

    @Column(name = "fecha_registro", columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP", updatable = false, nullable = false, insertable = false)
    private LocalDateTime fechaRegistro;

    // Relación muchos a uno: muchos usuarios tienen muchos roles
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    public String getNombreCompleto() {
        return this.apellidoPaterno + " " + this.apellidoMaterno + " " + this.nombre;
    }

}

