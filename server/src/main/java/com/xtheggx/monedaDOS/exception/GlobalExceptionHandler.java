package com.xtheggx.monedaDOS.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 - Error de validación en campos (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarArgumentoNoValido(MethodArgumentNotValidException ex,
                                                                  HttpServletRequest request) {
        Map<String, String> erroresCampo = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erroresCampo.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validación fallida",
                "Error en los campos enviados",
                request.getRequestURI(),
                erroresCampo
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 404 - Recurso no encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(ResourceNotFoundException ex,
                                                                    HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * 409 - Conflicto de integridad (ej. email duplicado, borrar categoría en uso)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarViolacionIntegridad(DataIntegrityViolationException ex,
                                                                    HttpServletRequest request) {
        String mensaje = "Conflicto de datos. La operación no puede completarse.";
        // Extraemos mensaje útil si es seguro
        if (ex.getRootCause() != null) {
            String detalle = ex.getRootCause().getMessage();
            // Filtro simple para no mostrar SQL crudo pero dar pistas
            if (detalle != null && !detalle.toLowerCase().contains("sql syntax")) {
                mensaje = "Error de integridad: " + detalle;
            }
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflicto de datos",
                mensaje,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * 400 - JSON malformado
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarMensajeNoLegible(HttpMessageNotReadableException ex,
                                                                 HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Formato inválido",
                "El cuerpo de la petición no es un JSON válido o tiene tipos incorrectos",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 400 - Argumento en URL inválido (ej. enviar letras en un ID numérico)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoArgumentoIncorrecto(MethodArgumentTypeMismatchException ex,
                                                                        HttpServletRequest request) {
        String mensaje = String.format("El valor '%s' no es válido para el parámetro '%s'",
                ex.getValue(), ex.getName());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Parámetro inválido",
                mensaje,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 500 - Error inesperado (Catch-all)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExcepcionGenerica(Exception ex, HttpServletRequest request) {
        // TODO: Loguear el error real en el servidor
        log.error("Error interno no controlado en {}: ", request.getRequestURI(), ex);

        // Al usuario le damos un mensaje genérico unificado
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                "Ocurrió un error inesperado. Por favor contacte al soporte.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    // --- CLASE DE RESPUESTA UNIFICADA ---
    @Getter
    @Setter
    public static class ErrorResponse {
        private String timestamp;
        private int status;
        private String error;     // Título corto del error
        private String message;   // Detalle legible
        private String path;      // URI donde ocurrió
        private Map<String, String> validationErrors; // Opcional, solo para validaciones

        // Constructor completo
        public ErrorResponse(int status, String error, String message, String path, Map<String, String> validationErrors) {
            this.timestamp = LocalDateTime.now().toString();
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.validationErrors = validationErrors;
        }

        // Constructor simple (sin errores de campo)
        public ErrorResponse(int status, String error, String message, String path) {
            this(status, error, message, path, null);
        }
    }
}