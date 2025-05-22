package com.recetas.recetasapp.service;

import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.entity.UsuarioRequestDTO;
import com.recetas.recetasapp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword()); // ¡Luego encripta!
        return usuarioRepository.save(usuario);
    }
}
