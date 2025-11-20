package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "CONFIGURACION_USUARIO")
@Data @NoArgsConstructor @AllArgsConstructor
public class ConfiguracionUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_config;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal p_necesidades;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal p_deseos;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal p_ahorro;
}
