package com.alquiler.coches.service;

import com.alquiler.coches.dto.DeleteResponse;
import com.alquiler.coches.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    UploadResponse uploadImage(MultipartFile file);
    DeleteResponse deleteImage(String publicId);
}
