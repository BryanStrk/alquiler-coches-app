package com.alquiler.coches.controller;

import com.alquiler.coches.dto.AuthResponseDTO;
import com.alquiler.coches.dto.UsuarioLoginDTO;
import com.alquiler.coches.dto.UsuarioRegisterDTO;
import com.alquiler.coches.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un JWT válido para la API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación correcta"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UsuarioLoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar cliente",
            description = "Crea un usuario con rol CLIENTE (los ADMIN se crean por seed)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario/email duplicado")
    })
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UsuarioRegisterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }
}
