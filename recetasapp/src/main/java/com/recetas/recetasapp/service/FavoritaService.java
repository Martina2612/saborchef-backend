package com.recetas.recetasapp.service;

import java.util.List;

import com.recetas.recetasapp.entity.Receta;

public interface FavoritaService {
    void agregarFavorita(Long idUsuario, Long idReceta);
    void eliminarFavorita(Long idUsuario, Long idReceta);
    List<Receta> listarFavoritas(Long idUsuario);
}

