package com.alquiler.coches.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Credenciales de inicio de sesión.
 */
public record UsuarioLoginDTO(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
