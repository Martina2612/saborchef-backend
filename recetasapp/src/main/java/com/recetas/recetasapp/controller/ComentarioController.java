package com.recetas.recetasapp.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.recetas.recetasapp.dto.*;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.service.ComentarioService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioRepository usuarioRepository;


    // GET /api/comentarios/{idReceta}
    @GetMapping("/{idReceta}")
    public ResponseEntity<List<ComentarioResponse>> listarComentarios(
            @PathVariable Long idReceta) {
        List<ComentarioResponse> lista = comentarioService.getComentariosPorReceta(idReceta);
        return ResponseEntity.ok(lista);
    }

    // POST /api/comentarios
    @PostMapping
    public ResponseEntity<ComentarioResponse> crearComentario(
            @RequestBody ComentarioRequest request,
            Principal principal) {
        // obtenemos idUsuario a partir del alias en principal
        Long idUsuario = usuarioRepository.findByAlias(principal.getName()).get().getId();
        ComentarioResponse response = comentarioService.agregarComentario(idUsuario, request);
        return ResponseEntity.ok(response);
    }
}

