package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.request.CalificacionRequest;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;
import com.recetas.recetasapp.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/comentar")
    public ResponseEntity<Void> comentar(Principal principal, @RequestBody ComentarioRequest request) {
        String username = principal.getName();
        Long idUsuario = usuarioRepository.findByAlias(username)
                .orElseThrow()
                .getId();

        calificacionService.comentar(idUsuario, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calificar")
    public ResponseEntity<Void> calificar(Principal principal, @RequestBody CalificacionRequest request) {
        String username = principal.getName();
        Long idUsuario = usuarioRepository.findByAlias(username)
                .orElseThrow()
                .getId();

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
}
