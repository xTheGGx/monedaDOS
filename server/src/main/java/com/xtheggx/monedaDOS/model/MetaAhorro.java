package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "META_AHORRO")
@Getter @Setter @ToString  @NoArgsConstructor @AllArgsConstructor
public class MetaAhorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_meta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta; // opcional

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto_objetivo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto_actual;

    private LocalDate fecha_limite; // opcional
}
