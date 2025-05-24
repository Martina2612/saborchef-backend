package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    Optional<Ingrediente> findByNombreIgnoreCase(String nombre);
}
