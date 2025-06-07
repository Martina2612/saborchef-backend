package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {
    List<Receta> findByUsuarioId(Long idUsuario);

    //Este va a servir para la screen de inicio que devuelve las 3 ultimas recetas subidas HABILITADAS
    List<Receta> findTop3ByHabilitadaTrueOrderByFechaCreacionDesc();

    // Para últimas recetas publicadas (12 más recientes)
    List<Receta> findTop12ByOrderByFechaCreacionDesc();

}
