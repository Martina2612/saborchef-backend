package com.recetas.recetasapp.service;

import java.util.Optional;

import com.recetas.recetasapp.dto.CronogramaDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;

public interface CronogramaCursoService {
    Optional<CronogramaDTO> obtenerPorId(Long id);
}

