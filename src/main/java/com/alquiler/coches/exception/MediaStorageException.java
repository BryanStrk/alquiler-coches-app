package com.alquiler.coches.exception;

import org.jspecify.annotations.Nullable;

/**
 * Error de negocio al subir o borrar medios (Cloudinary).
 *
 * <p>Hereda de {@link RuntimeException} (unchecked) a propósito: en Spring las
 * excepciones de negocio son unchecked para no "contaminar" las firmas con
 * {@code throws} por todas las capas. Se lanza aquí y se captura, ya
 * transformada en respuesta HTTP, en el {@code GlobalExceptionHandler}.</p>
 */
public class MediaStorageException extends RuntimeException {

    /** Error con solo mensaje (ej.: validación previa, "fichero vacío"). */
    public MediaStorageException(String message) {
        super(message);
    }

    /**
     * Error que envuelve la excepción original.
     *
     * @param message mensaje legible para nuestra capa
     * @param cause   excepción real (IOException, fallo del SDK...). Conservarla
     *                preserva el stacktrace de origen y facilita depurar.
     */
    public MediaStorageException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
