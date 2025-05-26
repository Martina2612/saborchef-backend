package com.recetas.recetasapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.service.FavoritaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favoritas")
@RequiredArgsConstructor
public class FavoritaController {

    private final FavoritaService favoritaService;

    @PostMapping("/{idUsuario}/{idReceta}")
    public ResponseEntity<?> agregar(@PathVariable Long idUsuario, @PathVariable Long idReceta) {
        favoritaService.agregarFavorita(idUsuario, idReceta);
        return ResponseEntity.ok("Agregada a favoritos");
    }

    @DeleteMapping("/{idUsuario}/{idReceta}")
    public ResponseEntity<?> eliminar(@PathVariable Long idUsuario, @PathVariable Long idReceta) {
        favoritaService.eliminarFavorita(idUsuario, idReceta);
        return ResponseEntity.ok("Eliminada de favoritos");
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<Receta>> listar(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(favoritaService.listarFavoritas(idUsuario));
    }
}

