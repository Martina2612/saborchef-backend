package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Categoria;
import com.recetas.recetasapp.entity.TipoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoRecetaRepository extends JpaRepository<TipoReceta, Long> {
    Optional<TipoReceta> findByDescripcion(Categoria descripcion);
}
