package com.alquiler.coches.dto;

import com.alquiler.coches.entity.EstadoCoche;
import com.alquiler.coches.entity.TipoCombustible;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload de entrada para crear o actualizar un coche.
 */
public record CocheRequestDTO(

        @NotBlank(message = "La marca es obligatoria")
        String marca,

        @NotBlank(message = "El modelo es obligatorio")
        String modelo,

        @NotBlank(message = "La matrícula es obligatoria")
        String matricula,

        @Min(value = 1990, message = "El año debe ser igual o posterior a 1990")
        @Max(value = 2027, message = "El año debe ser igual o anterior a 2027")
        int anio,

        @NotNull(message = "El precio por día es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio por día no puede ser negativo")
        @Positive(message = "El precio por día debe ser mayor que cero")
        BigDecimal precioPorDia,

        @NotNull(message = "El tipo de combustible es obligatorio")
        TipoCombustible tipoCombustible,

        @NotNull(message = "El estado es obligatorio")
        EstadoCoche estado,

        @PositiveOrZero(message = "Los kilómetros no pueden ser negativos")
        int kilometros,

        @Nullable String descripcion,

        @Nullable List<String> imageUrls
) {
}
