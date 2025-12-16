package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "DIVISA"
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Divisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_divisa")
    private Long idDivisa;

    @Column(name = "codigo_divisa",nullable = false, length = 3)
    private String codigoDivisa;

    @Column(name = "nombre_divisa",nullable = false, length = 100)
    private String nombreDivisa;

}
