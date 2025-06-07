package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.service.FavoritaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritas")
@RequiredArgsConstructor
public class FavoritaController {

    private final FavoritaService favoritaService;
    private final UsuarioRepository usuarioRepo;

    // Se obtiene el ID del usuario desde el token
    private Long getUsuarioId(Authentication auth) {
        String alias = auth.getName();
        Usuario user = usuarioRepo.findByAlias(alias)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    // Agregar a favoritos
    @PostMapping("/{idReceta}")
    public ResponseEntity<Void> agregarFavorita(@PathVariable Long idReceta, Authentication auth) {
        favoritaService.agregarFavorita(getUsuarioId(auth), idReceta);
        return ResponseEntity.ok().build();
    }

    // Eliminar de favoritos
    @DeleteMapping("/{idReceta}")
    public ResponseEntity<Void> eliminarFavorita(@PathVariable Long idReceta, Authentication auth) {
        favoritaService.eliminarFavorita(getUsuarioId(auth), idReceta);
        return ResponseEntity.ok().build();
    }

    // Listar favoritos
    @GetMapping
    public ResponseEntity<List<RecetaDetalleResponse>> listarFavoritas(Authentication auth) {
        List<RecetaDetalleResponse> favoritas = favoritaService.listarFavoritas(getUsuarioId(auth));
        return ResponseEntity.ok(favoritas);
    }
}

