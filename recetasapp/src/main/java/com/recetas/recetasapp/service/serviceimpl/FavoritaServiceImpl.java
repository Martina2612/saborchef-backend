package com.recetas.recetasapp.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.entity.Favorita;
import com.recetas.recetasapp.repository.FavoritaRepository;
import com.recetas.recetasapp.repository.RecetaRepository;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.service.FavoritaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritaServiceImpl implements FavoritaService {

    private final FavoritaRepository favoritaRepo;
    private final UsuarioRepository usuarioRepo;
    private final RecetaRepository recetaRepo;

    @Override
    public void agregarFavorita(Long idUsuario, Long idReceta) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Receta receta = recetaRepo.findById(idReceta)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        if (favoritaRepo.existsByUsuarioAndReceta(usuario, receta)) {
            throw new IllegalArgumentException("Ya está en favoritos");
        }

        Favorita favorita = Favorita.builder()
        .usuario(usuario)
        .receta(receta)
        .fechaAgregado(LocalDateTime.now())
        .build();

        favoritaRepo.save(favorita);
    }

    @Override
    public void eliminarFavorita(Long idUsuario, Long idReceta) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Receta receta = recetaRepo.findById(idReceta)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        favoritaRepo.deleteByUsuarioAndReceta(usuario, receta);
    }

    @Override
    public List<Receta> listarFavoritas(Long idUsuario) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return favoritaRepo.findByUsuario(usuario).stream()
                .map(Favorita::getReceta)
                .toList();
    }
}

