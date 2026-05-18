package com.alquiler.coches.controller;

import com.alquiler.coches.dto.MediaResponseDTO;
import com.alquiler.coches.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoints de medios. Esqueleto: delegan en CloudinaryService, que aún
 * lanza UnsupportedOperationException (→ 501 Not Implemented).
 */
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Subida de imágenes (pendiente de integrar Cloudinary)")
public class MediaController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Subir imagen",
            description = "Requiere rol ADMIN. No implementado: devuelve 501.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen subida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "501", description = "No implementado")
    })
    public ResponseEntity<MediaResponseDTO> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(cloudinaryService.uploadImage(file));
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar imagen",
            description = "Requiere rol ADMIN. No implementado: devuelve 501.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Imagen eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "501", description = "No implementado")
    })
    public ResponseEntity<Void> delete(@PathVariable String publicId) {
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.noContent().build();
    }
}
