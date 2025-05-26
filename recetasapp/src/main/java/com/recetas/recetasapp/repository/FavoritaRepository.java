package com.recetas.recetasapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.recetasapp.entity.Favorita;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Usuario;

@Repository
public interface FavoritaRepository extends JpaRepository<Favorita, Long> {

    boolean existsByUsuarioAndReceta(Usuario usuario, Receta receta);

    List<Favorita> findByUsuario(Usuario usuario);

    void deleteByUsuarioAndReceta(Usuario usuario, Receta receta);
}

