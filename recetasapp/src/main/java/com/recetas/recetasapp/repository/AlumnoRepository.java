package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Alumno;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByUsuarioId(Long usuarioId);
    Optional<Alumno> findByUsuario_Id(Long usuarioId);
}
