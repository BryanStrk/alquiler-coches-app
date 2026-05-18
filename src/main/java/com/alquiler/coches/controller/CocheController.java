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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear coche", description = "Requiere rol ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Coche creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<CocheResponseDTO> create(@Valid @RequestBody CocheRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cocheService.create(request));
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
