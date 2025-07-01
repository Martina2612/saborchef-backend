// ComentarioService.java
package com.recetas.recetasapp.service;

import java.util.List;
import com.recetas.recetasapp.dto.*;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;

public interface ComentarioService {
    List<ComentarioResponse> getComentariosPorReceta(Long idReceta);
    ComentarioResponse agregarComentario(Long idUsuario, ComentarioRequest request);
}

