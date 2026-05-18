package com.alquiler.coches.service;

import com.alquiler.coches.dto.MediaResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementación placeholder. NO implementar aquí: la integración real con
 * Cloudinary se hará manualmente con el SDK {@code cloudinary-http5}.
 */
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final String NOT_IMPLEMENTED =
            "TODO: implementar manualmente con SDK cloudinary-http5 cuando esté lista la integración";

    @Override
    public MediaResponseDTO uploadImage(MultipartFile file) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void deleteImage(String publicId) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
