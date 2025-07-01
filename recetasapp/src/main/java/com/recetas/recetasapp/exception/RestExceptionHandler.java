package com.recetas.recetasapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Intercepta las excepciones de la capa REST y configura la respuesta HTTP.
 */
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Void> handleDuplicate(DuplicateResourceException ex) {
        // Construye una respuesta 409 Conflict con header X-Existing-Recipe-Id
        return ResponseEntity
                .status(HttpStatus.CONFLICT.value())              // 409
                .header("X-Existing-Recipe-Id", ex.getExistingId().toString())
                .build();
    }
}
