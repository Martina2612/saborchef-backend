package com.recetas.recetasapp.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.entity.Calificacion;
import com.recetas.recetasapp.entity.Favorita;
import com.recetas.recetasapp.entity.Foto;
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
    @Transactional
    public void eliminarFavorita(Long idUsuario, Long idReceta) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Receta receta = recetaRepo.findById(idReceta)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        favoritaRepo.deleteByUsuarioAndReceta(usuario, receta);
    }

    @Override
    public List<RecetaDetalleResponse> listarFavoritas(Long idUsuario) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return favoritaRepo.findByUsuario(usuario).stream()
                .map(Favorita::getReceta)
                .map(this::mapToDetalle)
                .toList();
    }


    private RecetaDetalleResponse mapToDetalle(Receta receta) {
    RecetaDetalleResponse r = new RecetaDetalleResponse();
    r.setIdReceta(receta.getIdReceta());
    r.setNombre(receta.getNombreReceta());
    r.setDescripcion(receta.getDescripcionReceta());
    r.setFotoPrincipal(receta.getFotoPrincipal());
    r.setPorciones(receta.getPorciones());
    r.setTipo(receta.getTipo().getDescripcion().name());
    r.setNombreUsuario(receta.getUsuario().getUsername());
    r.setDuracion(receta.getDuracion());
    r.setFotos(receta.getFotos().stream().map(Foto::getUrlFoto).toList());

    // Calcular promedio de calificaciones
    double promedio = receta.getCalificaciones().stream()
            .mapToInt(Calificacion::getCalificacion)
            .average().orElse(0);
    r.setPromedioCalificacion(promedio);

    // Ingredientes
    r.setIngredientes(receta.getUtilizados().stream().map(u -> {
        RecetaDetalleResponse.IngredienteDetalle i = new RecetaDetalleResponse.IngredienteDetalle();
        i.setNombre(u.getIngrediente().getNombre());
        i.setCantidad(u.getCantidad());
        i.setUnidad(u.getUnidad().getDescripcion());
        i.setObservaciones(u.getObservaciones());
        return i;
    }).toList());

    // Pasos
    r.setPasos(receta.getPasos().stream().map(p -> {
        RecetaDetalleResponse.PasoDetalle paso = new RecetaDetalleResponse.PasoDetalle();
        paso.setNroPaso(p.getNroPaso());
        paso.setTexto(p.getTexto());
        paso.setContenidos(p.getContenidos().stream().map(c -> {
            RecetaDetalleResponse.Contenido cont = new RecetaDetalleResponse.Contenido();
            cont.setTipo(c.getTipoContenido());
            cont.setExtension(c.getExtension());
            cont.setUrl(c.getUrlContenido());
            return cont;
        }).toList());
        return paso;
    }).toList());

    // Comentarios (si la entidad los tiene)
    List<RecetaDetalleResponse.ComentarioResponse> comentarios = receta.getCalificaciones().stream()
        .filter(c -> c.getComentarios() != null && !c.getComentarios().isBlank())
        .map(c -> {
            RecetaDetalleResponse.ComentarioResponse dto = new RecetaDetalleResponse.ComentarioResponse();
            dto.setNombreUsuario(c.getUsuario().getNombre());
            dto.setTexto(c.getComentarios());
            return dto;
        }).toList();
    r.setComentarios(comentarios);

    return r;
}

}