package com.recetas.recetasapp.service;

import java.util.List;

import com.recetas.recetasapp.dto.CursoDisponibleDTO;

public interface CursoService {
    CursoDisponibleDTO obtenerCursoPorId(Long id);
    List<CursoDisponibleDTO> listarCursosDisponibles(Long idUsuario);
    
}
