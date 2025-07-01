package com.recetas.recetasapp.service;

import com.recetas.recetasapp.dto.request.CalificacionRequest;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;
import com.recetas.recetasapp.dto.response.TopRecetaResponse;

import java.util.List;

public interface CalificacionService {
    void calificar(Long idUsuario, CalificacionRequest request);
    Double obtenerPromedioCalificacion(Long idReceta);
    List<TopRecetaResponse> obtenerTopRecetas(int cantidad);
    int obtenerCalificacionUsuario(Long userId, Long recetaId);
}
