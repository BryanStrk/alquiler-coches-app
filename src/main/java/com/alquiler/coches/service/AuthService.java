package com.alquiler.coches.service;

import com.alquiler.coches.dto.AuthResponseDTO;
import com.alquiler.coches.dto.UsuarioLoginDTO;
import com.alquiler.coches.dto.UsuarioRegisterDTO;
import com.alquiler.coches.config.JwtUtil;
import com.alquiler.coches.entity.Role;
import com.alquiler.coches.entity.Usuario;
import com.alquiler.coches.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registro de clientes e inicio de sesión con emisión de JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public AuthResponseDTO login(UsuarioLoginDTO dto) {
        Usuario usuario = usuarioRepository.findByUsername(dto.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!passwordEncoder.matches(dto.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRole());
        return AuthResponseDTO.bearer(token, usuario.getUsername(), usuario.getRole());
    }

    @Transactional
    public AuthResponseDTO register(UsuarioRegisterDTO dto) {
        if (usuarioRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Usuario usuario = Usuario.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .nombre(dto.nombre())
                .apellidos(dto.apellidos())
                .role(Role.CLIENTE)
                .build();
        usuario = usuarioRepository.save(usuario);
        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRole());
        return AuthResponseDTO.bearer(token, usuario.getUsername(), usuario.getRole());
    }
}
