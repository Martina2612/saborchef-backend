package com.recetas.recetasapp.service;

import java.util.List;
import java.util.Optional;

import com.recetas.recetasapp.dto.CronogramaDTO;
import com.recetas.recetasapp.dto.CursoInscriptoDTO;
import com.recetas.recetasapp.entity.CronogramaCurso;

public interface CronogramaCursoService {
    Optional<CronogramaDTO> obtenerPorId(Long id);
    List<CursoInscriptoDTO> obtenerCursosInscriptoPorAlumno(Long idAlumno);

}

