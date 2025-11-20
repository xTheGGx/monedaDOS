// src/main/java/mx/dgtic/sfp/dto/RegistroDTO.java
package com.xtheggx.monedaDOS.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroDTO {
    @NotBlank private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;

    @NotBlank @Email private String email;

    @NotBlank @Size(min = 8) private String contrasena;
    @NotBlank private String confirmarContrasena;
}
