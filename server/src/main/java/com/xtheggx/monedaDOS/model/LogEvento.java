package com.xtheggx.monedaDOS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "LOG_EVENTO",
        indexes = {
                @Index(name = "idx_log_usuario_fecha", columnList = "usuario_id, fecha")
        }
)
@Data @NoArgsConstructor @AllArgsConstructor
public class LogEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_log;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // puede ser NULL

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 80)
    private String tipo_evento;

    @Column(columnDefinition = "TEXT")
    private String detalle;
}
