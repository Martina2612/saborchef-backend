package com.recetas.recetasapp.repository;

import com.recetas.recetasapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;



public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByAlias(String alias);
    Optional<Usuario> findById(Long id);

    boolean existsByEmail(String mail);
    boolean existsByAlias(String alias);
}
