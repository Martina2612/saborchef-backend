package com.recetas.recetasapp.service;

import com.recetas.recetasapp.entity.InscripcionCurso;

public interface InscripcionCursoService {
    InscripcionCurso inscribirAlumno(Long idCronograma, Long idAlumno);
}
