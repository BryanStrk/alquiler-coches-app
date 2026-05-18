package com.alquiler.coches.dto;

import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.TipoCombustible;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representación de salida de un coche.
 */
public record CocheResponseDTO(
        Long id,
        String marca,
        String modelo,
        String matricula,
        int anio,
        BigDecimal precioPorDia,
        TipoCombustible tipoCombustible,
        EstadoCoche estado,
        int kilometros,
        @Nullable String descripcion,
        List<String> imageUrls,
        @Nullable LocalDateTime createdAt,
        @Nullable LocalDateTime updatedAt
) {
}
