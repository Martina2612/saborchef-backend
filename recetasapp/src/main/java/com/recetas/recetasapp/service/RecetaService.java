package com.recetas.recetasapp.service;

import com.recetas.recetasapp.dto.request.RecetaCrearRequest;
import com.recetas.recetasapp.dto.request.RecetaFiltroRequest;
import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.dto.response.RecetaResumenResponse;
import com.recetas.recetasapp.entity.Receta;

import java.util.List;

public interface RecetaService {
    void crearReceta(RecetaCrearRequest request);
    void actualizarReceta(Long id, RecetaCrearRequest request);
    void eliminarReceta(Long id);
    RecetaDetalleResponse obtenerReceta(Long id);
    List<RecetaResumenResponse> listarRecetas(Long idUsuario, Long idTipo, String orden);
    List<RecetaResumenResponse> buscarPorNombre(String nombre, String orden);
    List<RecetaResumenResponse> buscarPorTipo(String tipo, String orden);
    List<RecetaResumenResponse> buscarPorIngrediente(String nombre, String orden);
    List<RecetaResumenResponse> buscarSinIngrediente(String nombre, String orden);
    List<RecetaResumenResponse> buscarPorUsuario(String nombreUsuario, String orden);
    List<RecetaResumenResponse> buscarPorFiltros(RecetaFiltroRequest filtro);
    List<RecetaDetalleResponse> obtenerUltimas3Recetas();
    List<RecetaDetalleResponse> obtenerUltimasRecetas();
}
