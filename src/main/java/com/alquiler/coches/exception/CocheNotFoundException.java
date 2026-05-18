package com.alquiler.coches.exception;

public class CocheNotFoundException extends RuntimeException {

    public CocheNotFoundException(Long id) {
        super("No se encontró ningún coche con id " + id);
    }

    public CocheNotFoundException(String message) {
        super(message);
    }
}
