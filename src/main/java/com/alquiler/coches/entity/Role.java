package com.alquiler.coches.entity;

/**
 * Rol de autorización de un usuario. Se persiste como STRING y se mapea a
 * la autoridad de Spring Security con el prefijo {@code ROLE_}.
 */
public enum Role {
    ADMIN,
    CLIENTE
}
