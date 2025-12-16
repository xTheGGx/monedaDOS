package com.xtheggx.monedaDOS.dto;

import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.model.CuentaTipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaResponse {

    private Long id;
    private String nombre;
    private String divisaCodigo;
    private CuentaTipo tipoCuenta;
    private BigDecimal saldoActual;

    public static CuentaResponse fromEntity(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getIdCuenta())
                .nombre(cuenta.getNombre())
                .divisaCodigo(
                        cuenta.getDivisa() != null
                                ? cuenta.getDivisa().getCodigoDivisa()
                                : null
                )
                .tipoCuenta(cuenta.getTipo())
                .saldoActual(cuenta.getSaldo())
                .build();
    }
}
