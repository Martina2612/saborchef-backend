package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Calificacion;
import com.recetas.recetasapp.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    List<Calificacion> findByReceta(Receta receta);
}

