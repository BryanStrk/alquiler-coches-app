package com.alquiler.coches.exception;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
