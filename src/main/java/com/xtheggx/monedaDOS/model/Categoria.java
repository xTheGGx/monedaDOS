package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "CATEGORIA",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_categoria", columnNames = {"usuario_id", "nombre"})
        }
)
@Data @NoArgsConstructor @AllArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    // NULL => global (por defecto)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 7) // INGRESO/EGRESO
    private CategoriaTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(length = 10) // NECESIDAD/DESEO/AHORRO o NULL
    private Clasificacion clasificacion;


}
