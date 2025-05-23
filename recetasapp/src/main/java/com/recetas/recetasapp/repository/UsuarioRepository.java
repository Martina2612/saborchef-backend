package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByAlias(String alias);

    boolean existsByEmail(String mail);
    boolean existsByAlias(String alias);
}
