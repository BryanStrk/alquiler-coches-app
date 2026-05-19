package com.alquiler.coches.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        // ObjectUtils.asMap es un helper del SDK para construir un Map literal
        // (clave, valor, clave, valor, ...) sin encadenar put() tras put().
        // "secure=true" fuerza que las URLs devueltas sean https://, no http://.
        Map<String, Object> config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret,
                "secure",     true
        );
        return new Cloudinary(config);
    }

}
