package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/* 
@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByUsuarioId(Long idUsuario);
    List<Receta> findByTipoIdTipo(Long idTipo);
    List<Receta> findByNombreRecetaContainingIgnoreCase(String nombre);
    List<Receta> findByTipoDescripcionIgnoreCase(String tipo);
    List<Receta> findByIngredientesContainingIgnoreCase(String ingrediente);
    List<Receta> findTop3ByOrderByFechaCreacionDesc();
}*/
