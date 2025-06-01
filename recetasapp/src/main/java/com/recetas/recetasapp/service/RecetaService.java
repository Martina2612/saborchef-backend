package com.recetas.recetasapp.service;

import com.recetas.recetasapp.dto.request.RecetaCrearRequest;
import com.recetas.recetasapp.dto.request.RecetaFiltroRequest;
import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.dto.response.RecetaEscaladaResponse;
import com.recetas.recetasapp.dto.response.RecetaResumenResponse;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Usuario;

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
 /**
     * Devuelve la receta escalada en base a un factor fijo. Por ejemplo factor=2.0 => doble de cantidad.
     */
    RecetaEscaladaResponse escalarRecetaPorFactor(Long idReceta, Double factor) throws Exception;

    /**
     * Devuelve la receta escalada en base a la cantidad de porciones deseadas.
     * Calcula factor = porcionesDeseadas / porcionesOriginales.
     */
    RecetaEscaladaResponse escalarRecetaPorPorciones(Long idReceta, Integer porcionesDeseadas) throws Exception;

    /**
     * Escala la receta a partir de la cantidad de UN solo ingrediente indicado. 
     * factor = nuevaCantidadIngresada / cantidadOriginalDelIngredienteElegido
     */
    RecetaEscaladaResponse escalarRecetaPorIngrediente(Long idReceta, Long ingredienteId, Double nuevaCantidad) throws Exception;

    // --- MÉTODOS PARA GUARDAR HASTA 10 RECETAS ESCALADAS POR USUARIO ---
    /**
     * Guarda en la base de datos la “versión escalada” de la receta (sin duplicar la receta completa),
     * sino guardando referencia a la receta original + factor de escalado + timestamp.
     * Si el usuario ya tiene 10 recetas guardadas, lanza excepción.
     */
    void guardarRecetaEscalada(Long idReceta, Usuario usuario, Double factor) throws Exception;

    /**
     * Devuelve la lista de recetas escaladas guardadas por un usuario (las últimas n o todas).
     */
    List<RecetaEscaladaResponse> listarRecetasEscaladasGuardadas(Usuario usuario);

    /**
     * Borra una receta guardada en particular (por id de RecetaGuardada).
     */
    void eliminarRecetaGuardada(Long idRecetaGuardada, Usuario usuario) throws Exception;
}
