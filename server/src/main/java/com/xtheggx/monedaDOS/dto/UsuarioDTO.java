// src/main/java/mx/dgtic/sfp/dto/UsuarioSesion.java
package com.xtheggx.monedaDOS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String contrasena;
    private String email;
    private String rol;
}
