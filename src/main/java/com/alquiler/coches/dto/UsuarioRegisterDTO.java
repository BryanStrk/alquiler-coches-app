package com.alquiler.coches.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload de registro de un nuevo cliente.
 */
public record UsuarioRegisterDTO(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @Email(message = "El email debe tener un formato válido")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos
) {
}
