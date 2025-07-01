package com.recetas.recetasapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.recetas.recetasapp.entity.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    // Sólo trae los habilitados para una receta
    List<Comentario> findByRecetaIdRecetaAndHabilitadoTrueOrderByFechaCreacionDesc(Long idReceta);
}
