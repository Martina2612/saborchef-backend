package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.dto.request.CalificacionRequest;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;
import com.recetas.recetasapp.dto.response.TopRecetaResponse;
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


    @PostMapping("/calificar")
    public ResponseEntity<Void> calificar(Principal principal, @RequestBody CalificacionRequest request) {
        String username = principal.getName();
        Long idUsuario = usuarioRepository.findByAlias(username)
                .orElseThrow()
                .getId();

        calificacionService.calificar(idUsuario, request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{idReceta}/promedio")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long idReceta) {
        return ResponseEntity.ok(calificacionService.obtenerPromedioCalificacion(idReceta));
    }

    @GetMapping("/top")
    public ResponseEntity<List<TopRecetaResponse>> obtenerTopRecetas(
            @RequestParam(name="cantidad", defaultValue="3") Integer cantidad) {
        List<TopRecetaResponse> top = calificacionService.obtenerTopRecetas(cantidad);
        return ResponseEntity.ok(top);
    }

    /**
     * Obtener la calificación que el usuario actual dio a la receta
     */
    @GetMapping("/{idReceta}/mi-calificacion")
    public ResponseEntity<Integer> obtenerMiCalificacion(@PathVariable Long idReceta, Principal principal) {
        String username = principal.getName();
        Long idUsuario = usuarioRepository.findByAlias(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        int calif = calificacionService.obtenerCalificacionUsuario(idUsuario, idReceta);
        return ResponseEntity.ok(calif);
    }

}
