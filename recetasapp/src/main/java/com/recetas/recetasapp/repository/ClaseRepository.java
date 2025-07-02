package com.recetas.recetasapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recetas.recetasapp.entity.Clase;

public interface ClaseRepository extends JpaRepository<Clase, Long>{
    List<Clase> findByCronograma_IdCronograma(Long idCronograma);

}
