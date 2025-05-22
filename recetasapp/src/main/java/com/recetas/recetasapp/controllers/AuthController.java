package com.recetas.recetasapp.controllers;

import com.recetas.recetasapp.entity.AuthRequestDTO;
import com.recetas.recetasapp.entity.AuthResponseDTO;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(request.getPassword())) {
            AuthResponseDTO response = new AuthResponseDTO();
            response.setMensaje("Login exitoso");
            response.setIdUsuario(usuarioOpt.get().getId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}
