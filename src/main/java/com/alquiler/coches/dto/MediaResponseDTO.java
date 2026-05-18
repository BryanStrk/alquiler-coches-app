package com.alquiler.coches.dto;

/**
 * Respuesta de una operación de subida de medios a Cloudinary.
 * (El servicio aún no está implementado: ver CloudinaryServiceImpl.)
 */
public record MediaResponseDTO(
        String publicId,
        String url
) {
}
