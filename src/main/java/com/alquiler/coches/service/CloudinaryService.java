package com.alquiler.coches.service;

import com.alquiler.coches.dto.MediaResponseDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstracción de almacenamiento de medios (Cloudinary).
 *
 * <p>Terreno preparado: la implementación aún no existe y lanzará
 * {@link UnsupportedOperationException} hasta que se integre el SDK.</p>
 */
public interface CloudinaryService {

    MediaResponseDTO uploadImage(MultipartFile file);

    void deleteImage(String publicId);
}
