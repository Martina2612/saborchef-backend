// ComentarioServiceImpl.java
package com.recetas.recetasapp.service.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.recetas.recetasapp.entity.*;
import com.recetas.recetasapp.dto.*;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;
import com.recetas.recetasapp.repository.*;
import com.recetas.recetasapp.service.ComentarioService;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.repository.RecetaRepository;

@Service
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepo;
    private final UsuarioRepository usuarioRepo;
    private final RecetaRepository recetaRepo;

    public ComentarioServiceImpl(
        ComentarioRepository comentarioRepo,
        UsuarioRepository usuarioRepo,
        RecetaRepository recetaRepo
    ) {
        this.comentarioRepo = comentarioRepo;
        this.usuarioRepo    = usuarioRepo;
        this.recetaRepo     = recetaRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioResponse> getComentariosPorReceta(Long idReceta) {
        return comentarioRepo
            .findByRecetaIdRecetaAndHabilitadoTrueOrderByFechaCreacionDesc(idReceta)
            .stream()
            .map(c -> ComentarioResponse.builder()
                .idComentario(c.getIdComentario())
                .nombreUsuario(c.getUsuario().getUsername())
                .texto(c.getTexto())
                .fechaCreacion(c.getFechaCreacion())
                .build()
            )
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComentarioResponse agregarComentario(Long idUsuario, ComentarioRequest request) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Receta receta = recetaRepo.findById(request.getIdReceta())
            .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        Comentario c = Comentario.builder()
            .usuario(usuario)
            .receta(receta)
            .texto(request.getTexto())
            .habilitado(false)
            .build();

        Comentario saved = comentarioRepo.save(c);
        return ComentarioResponse.builder()
            .idComentario(saved.getIdComentario())
            .nombreUsuario(usuario.getUsername())
            .texto(saved.getTexto())
            .fechaCreacion(saved.getFechaCreacion())
            .build();
    }
}

