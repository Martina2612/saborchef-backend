package com.recetas.recetasapp.service.serviceimpl;

import com.recetas.recetasapp.dto.request.CalificacionRequest;
import com.recetas.recetasapp.dto.request.ComentarioRequest;
import com.recetas.recetasapp.dto.response.ComentarioResponse;
import com.recetas.recetasapp.dto.response.TopRecetaResponse;
import com.recetas.recetasapp.entity.Calificacion;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Usuario;
import com.recetas.recetasapp.repository.CalificacionRepository;
import com.recetas.recetasapp.repository.RecetaRepository;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.service.CalificacionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;

    @Override
    @Transactional
    public void comentar(Long idUsuario, ComentarioRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();
        Receta receta = recetaRepository.findById(request.getIdReceta()).orElseThrow();

        Calificacion comentario = new Calificacion();
        comentario.setUsuario(usuario);
        comentario.setReceta(receta);
        comentario.setComentarios(request.getComentario());

        calificacionRepository.save(comentario);
    }

    @Override
    @Transactional
    public void calificar(Long idUsuario, CalificacionRequest request) {
        if (request.getCalificacion() < 1 || request.getCalificacion() > 5)
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5.");

        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();
        Receta receta = recetaRepository.findById(request.getIdReceta()).orElseThrow();

        Calificacion calificacion = new Calificacion();
        calificacion.setUsuario(usuario);
        calificacion.setReceta(receta);
        calificacion.setCalificacion(request.getCalificacion());

        calificacionRepository.save(calificacion);
    }

    @Override
    public List<ComentarioResponse> obtenerComentarios(Long idReceta) {
        Receta receta = recetaRepository.findById(idReceta).orElseThrow();
        return calificacionRepository.findByReceta(receta).stream()
                .filter(c -> c.getComentarios() != null && !c.getComentarios().isBlank())
                .map(c -> {
                    ComentarioResponse dto = new ComentarioResponse();
                    dto.setNombreUsuario(c.getUsuario().getNombre());
                    dto.setComentario(c.getComentarios());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Double obtenerPromedioCalificacion(Long idReceta) {
        Receta receta = recetaRepository.findById(idReceta).orElseThrow();
        List<Calificacion> calificaciones = calificacionRepository.findByReceta(receta);

        List<Integer> puntuaciones = calificaciones.stream()
                .map(Calificacion::getCalificacion)
                .filter(c -> c != null)
                .toList();

        if (puntuaciones.isEmpty()) return 0.0;

        return puntuaciones.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<TopRecetaResponse> obtenerTopRecetas(int cantidad) {
        Pageable topN = PageRequest.of(0, cantidad);
        List<Object[]> resultados = calificacionRepository.findTopRecetasConPromedio(topN);

        return resultados.stream().map(obj -> {
            Receta receta = (Receta) obj[0];
            Double promedio = (Double) obj[1];
            TopRecetaResponse dto = new TopRecetaResponse();
            dto.setIdReceta(receta.getIdReceta());
            dto.setNombreReceta(receta.getNombreReceta());
            dto.setFotoPrincipal(receta.getFotoPrincipal());
            dto.setPromedioCalificacion(promedio);
            return dto;
        }).toList();
    }

}
