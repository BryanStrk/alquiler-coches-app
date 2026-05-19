package com.alquiler.coches.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coches")
public class Coche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotBlank(message = "La matrícula es obligatoria")
    @Column(unique = true)
    private String matricula;

    @Min(value = 1990, message = "El año debe ser igual o posterior a 1990")
    @Max(value = 2027, message = "El año debe ser igual o anterior a 2027")
    private int anio;

    @DecimalMin(value = "0.0", message = "El precio por día no puede ser negativo")
    @Positive(message = "El precio por día debe ser mayor que cero")
    @Column(name = "precio_por_dia", precision = 10, scale = 2)
    private BigDecimal precioPorDia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustible", length = 20)
    private TipoCombustible tipoCombustible;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoCoche estado;

    @PositiveOrZero(message = "Los kilómetros no pueden ser negativos")
    private int kilometros;

    @Column(columnDefinition = "TEXT")
    private @Nullable String descripcion;

    /**
     * URLs de las imágenes del coche. Son los {@code secure_url} (https) que
     * devuelve Cloudinary al subir; este campo NO se manipula a mano desde el
     * cliente: lo rellena la capa de servicio a partir de {@code CloudinaryService}.
     *
     * <p>{@code @ElementCollection} mapea una colección de tipos básicos (aquí
     * {@code String}) en una TABLA APARTE, sin necesidad de crear una entidad
     * propia para cada URL. Hibernate crea la tabla {@code coche_imagenes} con
     * una FK {@code coche_id} hacia {@code coches}: una fila por imagen.</p>
     */
    // TODO: estos URLs serán poblados desde el frontend cuando Cloudinary esté
    // implementado en MediaController/CloudinaryService (actualmente lanza
    // UnsupportedOperationException). Por ahora se rellenan con placeholders en el seed.
    // fetch=EAGER: traemos las URLs junto con el coche (se necesitan casi siempre
    // al mostrarlo y son pocas) en vez de en una consulta extra perezosa.
    @ElementCollection(fetch = FetchType.EAGER)
    // @CollectionTable nombra esa tabla secundaria; @JoinColumn define la FK.
    @CollectionTable(name = "coche_imagenes", joinColumns = @JoinColumn(name = "coche_id"))
    // @Column: la columna que guarda cada URL; length=500 porque las URLs de
    // Cloudinary (con carpeta + transformaciones) son largas.
    @Column(name = "image_url", length = 500)
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private @Nullable LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private @Nullable LocalDateTime updatedAt;
}
