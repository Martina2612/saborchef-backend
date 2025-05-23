package com.recetas.recetasapp.service;

import com.recetas.recetasapp.*;
import com.recetas.recetasapp.dto.LoginRequest;
import com.recetas.recetasapp.dto.LoginResponse;
import com.recetas.recetasapp.entity.*;
import com.recetas.recetasapp.repository.*;
import com.recetas.recetasapp.security.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getMail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String jwt = jwtService.generateToken(
                Map.of("rol", usuario.getRol()),
                usuario.getMail()
        );

        return new LoginResponse(jwt);
    }
}
