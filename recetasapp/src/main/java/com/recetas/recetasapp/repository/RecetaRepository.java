package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecetaRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {
    List<Receta> findByUsuarioId(Long idUsuario);

    //Este va a servir para la screen de inicio que devuelve las 3 ultimas recetas subidas HABILITADAS
    List<Receta> findTop3ByHabilitadaTrueOrderByFechaCreacionDesc();

    // Para últimas recetas publicadas (12 más recientes)
    List<Receta> findTop12ByOrderByFechaCreacionDesc();

    Optional<Receta> findByUsuarioIdAndNombreRecetaIgnoreCase(Long usuarioId, String nombreReceta);


    @Query(
      "select r.nombreReceta " +
      "from Receta r " +
      "where r.habilitada = true " +
      "  and lower(r.nombreReceta) like lower(concat(:prefix, '%')) " +
      "order by r.nombreReceta asc"
    )
    List<String> findTop10ByNombreRecetaStartingWith(@Param("prefix") String prefix);
}
