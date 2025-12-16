package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.xtheggx.monedaDOS.model.Divisa;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
        name = "CUENTA",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_cuenta_usuario_nombre", columnNames = {"usuario_id", "nombre"})
        }
)
@Getter
@Setter
@ToString
@NoArgsConstructor 
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Long idCuenta;

    // Usuario dueño de la cuenta
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombre; // nombre de la cuenta (ej. "Efectivo MXN")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CuentaTipo tipo; // EFECTIVO, DEBITO, CREDITO, OTRO

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "dia_corte")
    @Min(value = 1, message = "El día de corte debe estar entre 1 y 31")
    @Max(value = 31, message = "El día de corte debe estar entre 1 y 31")
    private Integer diaCorte; // 1..31 o null
    @Column(name = "dia_pago")
    @Min(value = 1, message = "El día de pago debe estar entre 1 y 31")
    @Max(value = 31, message = "El día de pago debe estar entre 1 y 31")
    private Integer diaPago;  // 1..31 o null

    // Relación con Divisa en lugar de String moneda
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_divisa", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Divisa divisa;


    @Column(name = "limite_credito", precision = 15, scale = 2)
    private BigDecimal limiteCredito;

    @OneToMany(mappedBy = "cuenta")
    @JsonIgnore
    @ToString.Exclude
    private List<Transaccion> transacciones = new ArrayList<>();

    // Método helper para obtener el código de la divisa
    public String getCodigoDivisa() {
        return divisa != null ? divisa.getCodigoDivisa() : null;
    }


}
