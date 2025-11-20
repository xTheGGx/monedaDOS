package com.xtheggx.monedaDOS.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.xtheggx.monedaDOS.model.CuentaTipo;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    @NotBlank(message = "El nombre de la cuenta es obligatorio")
    @Size(max = 100, message = "El nombre de la cuenta debe tener máximo 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private CuentaTipo tipo;

    @Min(value = 1, message = "Día de corte debe ser entre 1 y 31")
    @Max(value = 31, message = "Día de corte debe ser entre 1 y 31")
    private Integer diaCorte;

    @Min(value = 1, message = "Día de pago debe ser entre 1 y 31")
    @Max(value = 31, message = "Día de pago debe ser entre 1 y 31")
    private Integer diaPago;

    @DecimalMin(value = "0.00", inclusive = true, message = "El límite de crédito no puede ser negativo")
    private BigDecimal limiteCredito;

    // Validación condicional: si es CREDITO, días obligatorios
    @AssertTrue(message = "Para cuentas de crédito, día de corte y de pago son obligatorios")
    public boolean isFechasCreditoValidas() {
        return tipo != CuentaTipo.CREDITO || (diaCorte != null && diaPago != null);
    }
}
