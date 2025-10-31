package com.xtheggx.monedaDOS.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorController {
    public static Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(final Throwable throwable, final Model model) {
        logger.error("Excepcion durante la ejecucion", throwable);
        String errorMessage = (throwable != null ? throwable.getMessage() : "Error desconocido");
        model.addAttribute("Mensajedeerror", errorMessage);
        model.addAttribute("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR); // Agregar HttpStatus al modelo
        return "error";
    }
}

