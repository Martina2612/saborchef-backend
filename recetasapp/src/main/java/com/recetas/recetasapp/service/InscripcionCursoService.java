package com.recetas.recetasapp.service;

import com.recetas.recetasapp.dto.BajaCursoResponse;
import com.recetas.recetasapp.entity.InscripcionCurso;

public interface InscripcionCursoService {
    InscripcionCurso inscribirAlumno(Long idCronograma, Long idAlumno);
    public BajaCursoResponse darDeBaja(Long idCronograma, Long idAlumno);
    public BajaCursoResponse calcularReintegroSinEjecutar(Long idCronograma, Long idAlumno);
}
