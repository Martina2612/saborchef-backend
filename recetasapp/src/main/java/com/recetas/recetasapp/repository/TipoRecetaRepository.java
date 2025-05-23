package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.TipoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoRecetaRepository extends JpaRepository<TipoReceta, Long> {
    
    // Para buscar un TipoReceta por descripción (nombre) ignorando mayúsculas/minúsculas
    Optional<TipoReceta> findByDescripcionIgnoreCase(String descripcion);
    
    // También podrías agregar otros métodos si los necesitas, por ejemplo:
    // List<TipoReceta> findByDescripcionContainingIgnoreCase(String descripcion);
}
