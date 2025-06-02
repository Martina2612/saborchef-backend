package com.recetas.recetasapp.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.recetasapp.entity.InscripcionCurso;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.CronogramaCurso;
import com.recetas.recetasapp.entity.InscripcionCurso;
@Repository
public interface InscripcionCursoRepository extends JpaRepository<InscripcionCurso, Long> {
    
    boolean existsByAlumnoAndCronograma(Alumno alumno, CronogramaCurso cronograma);
    Optional<InscripcionCurso> findByAlumno_IdAlumnoAndCronograma_IdCronograma(Long alumnoId, Long cronogramaId);
    List<InscripcionCurso> findByAlumno_IdAlumno(Long alumnoId);

}

