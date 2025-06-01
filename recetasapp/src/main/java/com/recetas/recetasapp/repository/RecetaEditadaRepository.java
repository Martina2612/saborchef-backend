package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.RecetaEditada;
import com.recetas.recetasapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaEditadaRepository extends JpaRepository<RecetaEditada, Long> {

    /**
     * Devuelve la lista de recetas guardadas por un usuario.
     */
    List<RecetaEditada> findAllByUsuario(Usuario usuario);

    /**
     * Cuenta cuántas recetas guardadas tiene un usuario.
     */
    Long countByUsuario(Usuario usuario);
}
