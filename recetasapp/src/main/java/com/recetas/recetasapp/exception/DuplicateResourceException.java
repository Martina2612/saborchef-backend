package com.recetas.recetasapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que indica que ya existe un recurso duplicado.
 * Contiene además el ID del recurso existente.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    private final Long existingId;

    public DuplicateResourceException(String message, Long existingId) {
        super(message);
        this.existingId = existingId;
    }

    public Long getExistingId() {
        return existingId;
    }
}
