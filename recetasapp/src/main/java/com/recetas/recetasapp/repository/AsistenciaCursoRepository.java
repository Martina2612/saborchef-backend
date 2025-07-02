package com.recetas.recetasapp.repository;


import com.recetas.recetasapp.entity.Alumno;
import com.recetas.recetasapp.entity.AsistenciaCurso;
import com.recetas.recetasapp.entity.Clase;
import com.recetas.recetasapp.entity.CronogramaCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface AsistenciaCursoRepository extends JpaRepository<AsistenciaCurso, Long> {

    //boolean existsByAlumnoAndCronogramaAndFecha(Alumno alumno, CronogramaCurso cronograma, Date fecha);
    int countByAlumno_IdAlumnoAndClase_Cronograma_IdCronograma(Long idAlumno, Long idCronograma);
    boolean existsByAlumnoAndClase(Alumno alumno, Clase clase);
}

