package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Utilizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilizadoRepository extends JpaRepository<Utilizado, Long> {

    List<Utilizado> findAllByRecetaIdReceta(Long idReceta);
}