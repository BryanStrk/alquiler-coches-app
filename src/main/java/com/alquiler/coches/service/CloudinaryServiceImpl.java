package com.alquiler.coches.service;

import com.alquiler.coches.dto.DeleteResponse;
import com.alquiler.coches.dto.UploadResponse;
import com.alquiler.coches.exception.MediaStorageException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementación real del almacenamiento de medios contra Cloudinary.
 *
 * <p>Es la pieza que habla con el SDK: traduce un {@link MultipartFile} de
 * Spring a una subida de Cloudinary y devuelve nuestros DTOs. Toda la
 * "fontanería" del proveedor (mapa de opciones, parseo de la respuesta,
 * manejo de errores) queda encapsulada aquí.</p>
 */
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    // Logger SLF4J asociado a esta clase. Sustituye a System.out.println:
//  - permite filtrar por niveles (TRACE < DEBUG < INFO < WARN < ERROR)
//  - añade automáticamente fecha, hora y nombre de la clase en cada mensaje
//  - se configura desde application.properties sin tocar código
// static final → existe una sola instancia compartida por toda la clase y no cambia nunca.
    private static final Logger log = LoggerFactory.getLogger(CloudinaryServiceImpl.class);

    // Carpeta lógica dentro de Cloudinary donde se agruparán todas las imágenes de esta app.
// Se extrae a constante para evitar "magic strings" repetidos: si mañana renombras la
// carpeta solo cambias aquí. Convención Java: las constantes van en MAYÚSCULAS_CON_GUION.
    private static final String FOLDER = "alquiler-coches";

    // Inyección por constructor: el campo es 'final' (no puede quedar a null ni
    // cambiar) y @RequiredArgsConstructor (Lombok) genera el constructor con este
    // parámetro. Es preferible a @Autowired sobre el campo porque deja la clase
    // testeable: en un test pasas un mock de Cloudinary por el constructor.
    private final Cloudinary cloudinary;

    /**
     * Sube una imagen a Cloudinary dentro de la carpeta del proyecto.
     *
     * @param file fichero recibido del cliente (parte multipart)
     * @return {@link UploadResponse} con public_id y URLs del recurso creado
     * @throws MediaStorageException si el fichero viene vacío, no se puede leer
     *         (I/O) o Cloudinary rechaza la subida
     */
    @Override
    public UploadResponse uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new MediaStorageException("El fichero está vacío");
        }

        // El public_id (nombre del recurso) lo decide el BACKEND con un UUID.
        // Nunca usamos el nombre original del cliente: evita colisiones (dos
        // "foto.jpg" se pisarían) y es más seguro (un nombre malicioso no
        // puede manipular rutas/sobrescribir otros recursos).
        String publicId = "%s/%s".formatted(FOLDER, UUID.randomUUID());

        // resource_type=auto: que Cloudinary detecte si es imagen, vídeo o "raw".
        // overwrite=false y use_filename=false refuerzan lo anterior: mandamos
        // nosotros sobre el nombre y no se sobrescribe nada existente.
        Map<String, Object> options = ObjectUtils.asMap(
                "public_id",     publicId,
                "resource_type", "auto",
                "overwrite",     false,
                "use_filename",  false
        );

        try {
            // OJO: file.getBytes() carga TODO el fichero en memoria. Para imágenes
            // (límite 10 MB) es asumible; para vídeo o ficheros grandes la mejora
            // sería uploadLarge() (subida por trozos / streaming).
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);

            String returnedId   = Objects.toString(result.get("public_id"), publicId);
            String url          = Objects.toString(result.get("url"), "");
            String secureUrl    = Objects.toString(result.get("secure_url"), url);
            String format       = Objects.toString(result.get("format"), "");
            long bytes          = result.get("bytes") instanceof Number n ? n.longValue() : 0L;
            @Nullable String resourceType = result.get("resource_type") != null
                    ? result.get("resource_type").toString()
                    : null;

            log.debug("Imagen subida a Cloudinary: {}", returnedId);
            return new UploadResponse(returnedId, url, secureUrl, format, bytes, resourceType);

            // Dos catch separados porque son fallos distintos:
            // - IOException: no pudimos LEER el fichero local (problema nuestro/del request).
            // - RuntimeException: el fichero estaba bien pero CLOUDINARY lo rechazó.
            // En ambos pasamos 'e' como segundo argumento (la "causa"): así el
            // stacktrace original no se pierde y se ve la excepción raíz en el log.
        } catch (IOException e) {
            log.error("Error de I/O leyendo el fichero antes de subirlo", e);
            throw new MediaStorageException("No se pudo leer el fichero", e);
        } catch (RuntimeException e) {
            log.error("Cloudinary rechazó la subida", e);
            throw new MediaStorageException("Error subiendo a Cloudinary", e);
        }
    }

    /**
     * Borra de Cloudinary el recurso identificado por {@code publicId}.
     *
     * @param publicId identificador devuelto al subir la imagen
     * @return {@link DeleteResponse} con el resultado informado por el proveedor
     * @throws MediaStorageException si el publicId es vacío o el borrado falla (I/O)
     */
    /**
     * Elimina una imagen de Cloudinary a partir de su {@code public_id}.
     *
     * <p>Flujo: valida el id → llama al SDK ({@code uploader().destroy(...)}) →
     * envuelve la respuesta en un {@link DeleteResponse}. Si Cloudinary devuelve
     * un resultado distinto de {@code "ok"} (por ejemplo {@code "not found"}),
     * NO lanza excepción: el borrado se considera idempotente y devuelve ese
     * estado al cliente para que decida qué hacer.
     *
     * @param publicId identificador único del recurso en Cloudinary (la ruta
     *                 lógica que se generó al subirlo, ej. "alquiler-coches/uuid").
     * @return {@link DeleteResponse} con el publicId y el resultado devuelto por Cloudinary.
     * @throws MediaStorageException si el publicId es nulo/vacío, o si hay un
     *                               error de I/O comunicándose con Cloudinary.
     */
    @Override
    public DeleteResponse deleteImage(String publicId) {

        // 1) Validación defensiva: fallamos RÁPIDO en local antes de hacer una
        //    llamada de red inútil a Cloudinary si el dato está mal.
        //    isBlank() cubre null y también strings con solo espacios.
        if (publicId == null || publicId.isBlank()) {
            throw new MediaStorageException("public_id obligatorio");
        }

        try {
            // 2) Llamada real al SDK de Cloudinary.
            //    "resource_type=auto" → deja que Cloudinary detecte si es imagen,
            //    vídeo o raw (PDF, ZIP…). Si pusiéramos "image" y el asset fuese
            //    un vídeo, Cloudinary respondería "not found" aunque sí existe.
            //
            //    Map<?, ?>: wildcard. La respuesta del SDK es un Map genérico
            //    sin tipo concreto; usamos comodín para evitar warnings.
            Map<?, ?> result = cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.asMap("resource_type", "auto"));

            // 3) Extraemos el campo "result" de la respuesta de Cloudinary.
            //    Valores posibles: "ok" (borrado), "not found" (ya no estaba), etc.
            //    Objects.toString(valor, default) → blindaje contra NullPointerException:
            //    si get("result") devuelve null, usamos "unknown" como fallback.
            String outcome = Objects.toString(result.get("result"), "unknown");

            // 4) Log en nivel DEBUG (no INFO porque es un detalle de trazabilidad
            //    de uso normal, no un evento crítico).
            //    Los {} son placeholders parametrizados de SLF4J: más eficientes
            //    y seguros que concatenar con + porque solo formatean si el nivel
            //    DEBUG está habilitado.
            log.debug("Imagen eliminada de Cloudinary: {} ({})", publicId, outcome);

            return new DeleteResponse(publicId, outcome);

        } catch (IOException e) {
            // 5) IOException = problema de red/comunicación con la API de Cloudinary.
            //    Logueamos con stacktrace completo (pasando 'e' como último arg)
            //    para poder diagnosticar el fallo en producción.
            log.error("Error eliminando {} de Cloudinary", publicId, e);

            // 6) Re-lanzamos como excepción de nuestro dominio, pero PRESERVANDO
            //    la causa original (el segundo parámetro 'e'). Así el stacktrace
            //    completo viaja hasta el GlobalExceptionHandler y no se pierde
            //    información de debugging. Sin esto sería un anti-patrón llamado
            //    "exception swallowing".
            throw new MediaStorageException("No se pudo eliminar el recurso", e);
        }
    }
}
