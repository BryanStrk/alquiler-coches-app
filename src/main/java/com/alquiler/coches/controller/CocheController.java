package com.alquiler.coches.controller;

import com.alquiler.coches.dto.CocheRequestDTO;
import com.alquiler.coches.dto.CocheResponseDTO;
import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.TipoCombustible;
import com.alquiler.coches.service.CocheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/coches")
@RequiredArgsConstructor
@Tag(name = "Coches", description = "Gestión de la flota de vehículos")
public class CocheController {

    private final CocheService cocheService;

    @GetMapping
    @Operation(summary = "Listar coches",
            description = "Listado público con filtros opcionales por tipo, estado y marca")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Listado obtenido"))
    public ResponseEntity<List<CocheResponseDTO>> getAll(
            @RequestParam(required = false) @Nullable TipoCombustible tipo,
            @RequestParam(required = false) @Nullable EstadoCoche estado,
            @RequestParam(required = false) @Nullable String marca) {
        return ResponseEntity.ok(cocheService.findAll(tipo, estado, marca));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar coches disponibles",
            description = "Devuelve únicamente los coches en estado DISPONIBLE")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Listado obtenido"))
    public ResponseEntity<List<CocheResponseDTO>> getDisponibles() {
        return ResponseEntity.ok(cocheService.findDisponibles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener coche por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coche encontrado"),
            @ApiResponse(responseCode = "404", description = "Coche no encontrado")
    })
    public ResponseEntity<CocheResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cocheService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear coche con imágenes",
            description = "Requiere rol ADMIN. multipart/form-data: parte 'coche' "
                    + "(JSON CocheRequestDTO) y parte opcional 'imagenes' (0..N ficheros). "
                    + "Las imágenes se suben a Cloudinary antes de persistir; si la "
                    + "persistencia falla se hace rollback de las imágenes subidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Coche creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "500", description = "Error subiendo a Cloudinary")
    })
    public ResponseEntity<CocheResponseDTO> create(
            @RequestPart("coche") @Valid CocheRequestDTO request,
            @RequestPart(value = "imagenes", required = false)
            @Nullable List<MultipartFile> imagenes) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cocheService.create(request, imagenes));
    }

    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Añadir imágenes a un coche existente",
            description = "Requiere rol ADMIN. Sube las imágenes a Cloudinary y las "
                    + "AÑADE (no reemplaza) a las del coche. Rollback de las recién "
                    + "subidas si la actualización falla.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imágenes añadidas"),
            @ApiResponse(responseCode = "400", description = "Petición inválida"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "Coche no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error subiendo a Cloudinary")
    })
    public ResponseEntity<CocheResponseDTO> addImages(
            @PathVariable Long id,
            @RequestPart("imagenes") List<MultipartFile> imagenes) {
        return ResponseEntity.ok(cocheService.addImages(id, imagenes));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar coche", description = "Requiere rol ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coche actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "Coche no encontrado")
    })
    public ResponseEntity<CocheResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody CocheRequestDTO request) {
        return ResponseEntity.ok(cocheService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar coche", description = "Requiere rol ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Coche eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos"),
            @ApiResponse(responseCode = "404", description = "Coche no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cocheService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
