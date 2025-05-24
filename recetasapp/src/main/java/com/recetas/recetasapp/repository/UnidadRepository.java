package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {
    Optional<Unidad> findByDescripcionIgnoreCase(String descripcion);
}
