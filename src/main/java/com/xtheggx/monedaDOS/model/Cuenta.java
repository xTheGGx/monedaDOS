package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "CUENTA",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_cuenta_usuario_nombre", columnNames = {"usuario_id", "nombre"})
        }
)
@Data @NoArgsConstructor @AllArgsConstructor
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Integer idCuenta;

    // Usuario duenio de la cuenta
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombre; // nombre de la cuenta (ej. "Efectivo MXN")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CuentaTipo tipo; // EFECTIVO, DEBITO, CREDITO, OTRO

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false, length = 3)
    private String moneda; // ISO 4217 (MXN, USD, ...)
    @Column(name = "dia_corte")
    private Integer diaCorte; // 1..31 o null
    @Column(name = "dia_pago")
    private Integer diaPago;  // 1..31 o null

    @Column(name = "limite_credito", precision = 15, scale = 2)
    private BigDecimal limiteCredito;

    @OneToMany(mappedBy = "cuenta")
    private List<Transaccion> transacciones = new ArrayList<>();

}
