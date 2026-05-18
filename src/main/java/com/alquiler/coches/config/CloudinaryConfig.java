package com.alquiler.coches.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Placeholder de configuración de Cloudinary.
 *
 * <p>El terreno está preparado pero la integración NO está implementada.
 * Cuando se aborde manualmente con el SDK {@code cloudinary-http5}, descomentar
 * el bean siguiente y devolver una instancia de {@code com.cloudinary.Cloudinary}
 * construida a partir de estas propiedades.</p>
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    // TODO: implementar manualmente cuando esté lista la integración con Cloudinary.
    //
    // @Bean
    // public Cloudinary cloudinary() {
    //     return new Cloudinary(ObjectUtils.asMap(
    //             "cloud_name", cloudName,
    //             "api_key", apiKey,
    //             "api_secret", apiSecret,
    //             "secure", true));
    // }
}
