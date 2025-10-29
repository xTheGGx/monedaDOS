package com.xtheggx.monedaDOS.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {

    @NotNull(message = "El id de la cuenta es obligatorio")
    private Integer cuentaId;

    @NotNull(message = "El id de la categoría es obligatorio")
    private Integer categoriaId;

    @NotNull(message = "El id del usuario es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "El monto de la transacción es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto no puede ser cero")
    private java.math.BigDecimal monto;

    @Size(max = 255, message = "La descripción debe tener máximo 255 caracteres")
    private String descripcion;


}
