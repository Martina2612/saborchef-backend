package com.recetas.recetasapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.recetasapp.entity.InscripcionCurso;

@Repository
public interface InscripcionCursoRepository extends JpaRepository<InscripcionCurso, Long> {

    Optional<InscripcionCurso> findByAlumnoIdAndCronogramaId(Long alumnoId, Long cronogramaId);

    List<InscripcionCurso> findByAlumnoId(Long alumnoId);
}

