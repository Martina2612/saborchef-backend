package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.request.CalificacionRequest;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;
import com.recetas.recetasapp.dto.response.TopRecetaResponse;
import com.recetas.recetasapp.service.CalificacionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionService calificacionService;

    @PostMapping("/comentar")
    public ResponseEntity<Void> comentar(Principal principal, @RequestBody ComentarioRequest request) {
        Long idUsuario = Long.parseLong(principal.getName()); // Asegúrate que el Principal devuelve el ID
        calificacionService.comentar(idUsuario, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calificar")
    public ResponseEntity<Void> calificar(Principal principal, @RequestBody CalificacionRequest request) {
        Long idUsuario = Long.parseLong(principal.getName());
        calificacionService.calificar(idUsuario, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idReceta}/comentarios")
    public ResponseEntity<List<ComentarioResponse>> obtenerComentarios(@PathVariable Long idReceta) {
        return ResponseEntity.ok(calificacionService.obtenerComentarios(idReceta));
    }

    @GetMapping("/{idReceta}/promedio")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long idReceta) {
        return ResponseEntity.ok(calificacionService.obtenerPromedioCalificacion(idReceta));
    }

    @GetMapping("/top")
    public ResponseEntity<List<TopRecetaResponse>> obtenerTopRecetas(
            @RequestParam(defaultValue = "12") int cantidad) {
        return ResponseEntity.ok(calificacionService.obtenerTopRecetas(cantidad));
    }

}
