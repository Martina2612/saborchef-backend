package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.LoginRequest;
import com.recetas.recetasapp.dto.LoginResponse;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.security.JwtUtil;
import com.recetas.recetasapp.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Usuario usuario = usuarioService.login(request.getMail(), request.getPassword());
            String token = jwtUtil.generateToken(usuario.getMail(), usuario.getRol().name());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas: " + e.getMessage());
        }
    }

    // Otros endpoints como /registro, /verificar, etc.
}
