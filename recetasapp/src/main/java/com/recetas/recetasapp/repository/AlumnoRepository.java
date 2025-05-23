package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    // Podés agregar métodos personalizados si querés después
}
