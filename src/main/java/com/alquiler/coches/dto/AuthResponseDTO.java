package com.alquiler.coches.dto;

import com.alquiler.coches.entity.Role;

/**
 * Respuesta de autenticación con el JWT emitido.
 */
public record AuthResponseDTO(
        String token,
        String tokenType,
        String username,
        Role role
) {
    public static AuthResponseDTO bearer(String token, String username, Role role) {
        return new AuthResponseDTO(token, "Bearer", username, role);
    }
}
