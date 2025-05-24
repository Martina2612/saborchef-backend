package com.recetas.recetasapp.service;

import com.recetas.recetasapp.dto.request.CalificacionRequest;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;

import java.util.List;

public interface CalificacionService {
    void comentar(Long idUsuario, ComentarioRequest request);
    void calificar(Long idUsuario, CalificacionRequest request);
    List<ComentarioResponse> obtenerComentarios(Long idReceta);
    Double obtenerPromedioCalificacion(Long idReceta);
}
