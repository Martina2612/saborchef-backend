package com.recetas.recetasapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.InscripcionCurso;

public interface InscripcionCursoRepository extends JpaRepository<InscripcionCurso, Long> {
    
    boolean existsByAlumnoAndCronograma(Alumno alumno, CronogramaCurso cronograma);

}
