
package com.recetas.recetasapp.service;

import java.util.List;

import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;


public interface FavoritaService {
    void agregarFavorita(Long idUsuario, Long idReceta);
    void eliminarFavorita(Long idUsuario, Long idReceta);
    List<RecetaDetalleResponse> listarFavoritas(Long idUsuario);
}
