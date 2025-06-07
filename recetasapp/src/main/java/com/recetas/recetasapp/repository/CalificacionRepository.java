package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Calificacion;
import com.recetas.recetasapp.entity.Receta;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    List<Calificacion> findByReceta(Receta receta);

    @Query("SELECT c.receta AS receta, AVG(c.calificacion) AS promedio " +
       "FROM Calificacion c " +
       "WHERE c.calificacion IS NOT NULL " +
       "GROUP BY c.receta " +
       "ORDER BY promedio DESC")
    List<Object[]> findTopRecetasConPromedio(Pageable pageable);
}