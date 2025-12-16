package com.xtheggx.monedaDOS.dto;

import com.xtheggx.monedaDOS.model.CuentaTipo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCuentaDTO {

    @NotBlank(message = "El nombre de la cuenta es obligatorio")
    private String nombre;

    @NotNull(message = "La divisa es obligatoria")
    private Long divisaId;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private CuentaTipo tipo;

    // Opcionales: sólo tienen sentido si tipo = CREDITO
    @Min(value = 1, message = "Día de corte debe ser entre 1 y 31")
    @Max(value = 31, message = "Día de corte debe ser entre 1 y 31")
    private Integer diaCorte;

    @Min(value = 1, message = "Día de pago debe ser entre 1 y 31")
    @Max(value = 31, message = "Día de pago debe ser entre 1 y 31")
    private Integer diaPago;

    @DecimalMin(value = "0.00", inclusive = true,
            message = "El límite de crédito no puede ser negativo")
    private BigDecimal limiteCredito;

    @AssertTrue(message = "Para cuentas de crédito, día de corte y de pago son obligatorios")
    public boolean isFechasCreditoValidas() {
        if (tipo != CuentaTipo.CREDITO) {
            return true;
        }
        return diaCorte != null && diaPago != null;
    }
}
