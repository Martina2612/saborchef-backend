package com.recetas.recetasapp.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.recetas.recetasapp.entity.CronogramaCurso;

public interface CronogramaCursoRepository extends JpaRepository<CronogramaCurso, Long> {
    List<CronogramaCurso> findByFechaInicioAfter(Date fechaActual);
    List<CronogramaCurso> findByCurso_IdCurso(Long idCurso);
    //List<CronogramaCurso> findByInscripciones_Alumno_Id(Long idAlumno);


}