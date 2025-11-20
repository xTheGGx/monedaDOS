package com.xtheggx.monedaDOS.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Maneja errores de validación (Bean Validation) en request bodies */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarArgumentoNoValido(MethodArgumentNotValidException ex) {
        // Extraer errores de campo
        Map<String, String> erroresCampo = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erroresCampo.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        // Construir respuesta de error
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Error de validación en los campos enviados",
                erroresCampo
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** Maneja excepciones de recurso no encontrado (404) */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /** Maneja violaciones de integridad de datos o conflictos (409) */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarViolacionIntegridad(DataIntegrityViolationException ex) {
        // Mensaje genérico de conflicto, o usar mensaje de la causa si es comprensible
        String mensaje = "Conflicto con el estado de los datos";
        if (ex.getRootCause() != null) {
            String detalle = ex.getRootCause().getMessage();
            // Evitar exponer SQL, pero si es mensaje legible lo incluimos
            if (detalle != null && !detalle.startsWith("ERROR:")) {
                mensaje = detalle;
            }
        }
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                mensaje,
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /** Maneja errores de formato JSON no legible o tipos inválidos en el cuerpo (400) */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarMensajeNoLegible(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Formato JSON inválido o datos no interpretables",
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** Maneja errores de tipo de argumento (por ejemplo, enum/numero en RequestParam) (400) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoArgumentoIncorrecto(MethodArgumentTypeMismatchException ex) {
        String nombreParam = ex.getName();
        String mensaje = String.format("Valor inválido para el parámetro '%s'", nombreParam);
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                mensaje,
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** Maneja cualquier otra excepción no controlada (500) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarExcepcionGenerica(Exception ex, HttpServletRequest req) {
        ApiError body = new ApiError(Instant.now().toString(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    public record ApiError(String timestamp, String path, String message) {
    }

    // Clase interna o separada para el cuerpo de error unificado
    static class ErrorResponse {
        private final String timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final Map<String, String> fields;

        public ErrorResponse(String timestamp, int status, String error, String message, Map<String, String> fields) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.fields = fields;
        }

    }
}
