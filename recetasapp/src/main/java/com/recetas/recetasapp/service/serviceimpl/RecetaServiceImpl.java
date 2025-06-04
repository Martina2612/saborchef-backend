package com.recetas.recetasapp.service.serviceimpl;

import com.recetas.recetasapp.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


import com.recetas.recetasapp.dto.request.RecetaCrearRequest;
import com.recetas.recetasapp.dto.request.RecetaFiltroRequest;
import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.dto.response.RecetaResumenResponse;
import com.recetas.recetasapp.entity.*;
import com.recetas.recetasapp.exception.ResourceNotFoundException;
import com.recetas.recetasapp.repository.IngredienteRepository;
import com.recetas.recetasapp.repository.RecetaRepository;
import com.recetas.recetasapp.repository.TipoRecetaRepository;
import com.recetas.recetasapp.repository.UnidadRepository;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.service.RecetaService;
import com.recetas.recetasapp.specification.RecetaSpecifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.recetas.recetasapp.specification.RecetaSpecifications.*;

@Service
public class RecetaServiceImpl implements RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TipoRecetaRepository tipoRecetaRepository;
    @Autowired
    private IngredienteRepository ingredienteRepository;
    @Autowired
    private UnidadRepository unidadRepository;

    @Override
public List<RecetaDetalleResponse> obtenerUltimas3Recetas() {
    List<Receta> recetas = recetaRepository.findTop3ByHabilitadaTrueOrderByFechaCreacionDesc();
    return recetas.stream()
            .map(this::mapToDetalle)
            .toList();
}

    @Override
    public List<RecetaResumenResponse> listarRecetas(Long u, Long t, String o) {
        var spec = Specification.<Receta>where(null);
        if (u!=null) spec = spec.and(conUsuario(u));
        if (t!=null) spec = spec.and(conTipoId(t));
        return recetaRepository.findAll(spec, Sort.unsorted()).stream()
            .map(this::mapToResumen).sorted(getComparator(o)).collect(Collectors.toList());
    }

    @Override
    public RecetaDetalleResponse obtenerReceta(Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada"));
        return mapToDetalle(receta);
    }

    @Override
    public void eliminarReceta(Long id) {
        if (!recetaRepository.existsById(id))
            throw new ResourceNotFoundException("Receta no encontrada");
        recetaRepository.deleteById(id);
    }

    @Override
    public void crearReceta(RecetaCrearRequest req) {
        var usuario = usuarioRepository.findById(req.getIdUsuario())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Categoria cat;
        try {
            cat = Categoria.valueOf(req.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Categoría inválida: " + req.getTipo());
        }

        var tipo = tipoRecetaRepository.findByDescripcion(cat)
            .orElseGet(() -> tipoRecetaRepository.save(new TipoReceta(null, cat)));

        // Crear la receta sin relaciones primero
        var recetaNueva = new Receta();
        recetaNueva.setUsuario(usuario);
        recetaNueva.setTipo(tipo);
        recetaNueva.setNombreReceta(req.getNombreReceta());
        recetaNueva.setDescripcionReceta(req.getDescripcionReceta());
        recetaNueva.setFotoPrincipal(req.getFotoPrincipal());
        recetaNueva.setCantidadPersonas(req.getCantidadPersonas());
        recetaNueva.setFechaCreacion(LocalDateTime.now());

        // Guardar la receta para que tenga ID (necesario para las relaciones)
        var recetaGuardada = recetaRepository.save(recetaNueva);

        // Mapear ingredientes utilizados y asignar recetaGuardada
        var utilizados = req.getIngredientes().stream().map(ic -> {
            var ingr = ingredienteRepository.findByNombreIgnoreCase(ic.getNombreIngrediente())
                .orElseGet(() -> ingredienteRepository.save(new Ingrediente(null, ic.getNombreIngrediente())));

            var uniDesc = ic.getUnidad().toLowerCase(Locale.ROOT);
            var valid = List.of("kg", "gr", "ml", "litros", "unidad");
            if (!valid.contains(uniDesc)) {
                throw new ResourceNotFoundException("Unidad inválida: " + ic.getUnidad());
            }
            var unidad = unidadRepository.findByDescripcionIgnoreCase(uniDesc)
                .orElseGet(() -> unidadRepository.save(new Unidad(null, uniDesc)));

            var u = new Utilizado();
            u.setIngrediente(ingr);
            u.setUnidad(unidad);
            u.setCantidad(ic.getCantidad());
            u.setObservaciones(ic.getObservaciones());
            u.setReceta(recetaGuardada);
            return u;
        }).collect(Collectors.toList());
        recetaGuardada.setUtilizados(utilizados);

        // Mapear pasos y asignar recetaGuardada
        var pasos = req.getPasos().stream().map(pc -> {
            var p = new Pasos();
            p.setNroPaso(pc.getNroPaso());
            p.setTexto(pc.getTexto());
            p.setReceta(recetaGuardada);

            var cont = pc.getContenidos().stream().map(mc -> {
                var m = new Multimedia();
                m.setTipoContenido(mc.getTipoContenido());
                m.setExtension(mc.getExtension());
                m.setUrlContenido(mc.getUrlContenido());
                m.setPaso(p);
                return m;
            }).collect(Collectors.toList());
            p.setContenidos(cont);
            return p;
        }).collect(Collectors.toList());
        recetaGuardada.setPasos(pasos);

        // Mapear fotos y asignar recetaGuardada
        if (req.getFotos() != null) {
            var fotos = req.getFotos().stream().map(fc -> {
                var f = new Foto();
                f.setUrlFoto(fc.getUrlFoto());
                f.setExtension(getExtension(fc.getUrlFoto()));
                f.setDescripcion(fc.getDescripcion());
                f.setReceta(recetaGuardada);
                return f;
            }).collect(Collectors.toList());
            recetaGuardada.setFotos(fotos);
        }

        // Guardar nuevamente con las relaciones ya asignadas
        recetaRepository.save(recetaGuardada);
    }



    @Override
    public void actualizarReceta(Long id, RecetaCrearRequest request) {
        Receta existing = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada"));

        // Actualiza campos según el request
        existing.setNombreReceta(request.getNombreReceta());
        existing.setDescripcionReceta(request.getDescripcionReceta());
        existing.setFotoPrincipal(request.getFotoPrincipal());
        existing.setCantidadPersonas(request.getCantidadPersonas());

        // Actualiza tipo
        Categoria cat;
        try {
            cat = Categoria.valueOf(request.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Categoría inválida: " + request.getTipo());
        }
        var tipo = tipoRecetaRepository.findByDescripcion(cat)
                .orElseGet(() -> tipoRecetaRepository.save(new TipoReceta(null, cat)));
        existing.setTipo(tipo);

        // Actualiza usuario si necesario, o dejar igual (normalmente no se cambia)
        // existing.setUsuario(usuario);

        // Actualizar ingredientes utilizados (eliminas los anteriores y agregas nuevos)
        existing.getUtilizados().clear();
        var utilizados = request.getIngredientes().stream().map(ic -> {
            var ingr = ingredienteRepository.findByNombreIgnoreCase(ic.getNombreIngrediente())
                    .orElseGet(() -> ingredienteRepository.save(new Ingrediente(null, ic.getNombreIngrediente())));

            var uniDesc = ic.getUnidad().toLowerCase(Locale.ROOT);
            var valid = List.of("kg","gr","ml","litros","unidad");
            if (!valid.contains(uniDesc)) {
                throw new ResourceNotFoundException("Unidad inválida: " + ic.getUnidad());
            }
            var unidad = unidadRepository.findByDescripcionIgnoreCase(uniDesc)
                    .orElseGet(() -> unidadRepository.save(new Unidad(null, uniDesc)));

            var u = new Utilizado();
            u.setIngrediente(ingr);
            u.setUnidad(unidad);
            u.setCantidad(ic.getCantidad());
            u.setObservaciones(ic.getObservaciones());
            u.setReceta(existing);
            return u;
        }).collect(Collectors.toList());
        existing.setUtilizados(utilizados);

        // Actualizar pasos similar a ingredientes
        existing.getPasos().clear();
        var pasos = request.getPasos().stream().map(pc -> {
            var p = new Pasos();
            p.setNroPaso(pc.getNroPaso());
            p.setTexto(pc.getTexto());
            p.setReceta(existing);
            var cont = pc.getContenidos().stream().map(mc -> {
                var m = new Multimedia();
                m.setTipoContenido(mc.getTipoContenido());
                m.setExtension(mc.getExtension());
                m.setUrlContenido(mc.getUrlContenido());
                m.setPaso(p);
                return m;
            }).collect(Collectors.toList());
            p.setContenidos(cont);
            return p;
        }).collect(Collectors.toList());
        existing.setPasos(pasos);

        // Fotos
        existing.getFotos().clear();
        if (request.getFotos() != null) {
            var fotos = request.getFotos().stream().map(fc -> {
                var f = new Foto();
                f.setUrlFoto(fc.getUrlFoto());
                f.setExtension(getExtension(fc.getUrlFoto()));
                f.setReceta(existing);
                return f;
            }).collect(Collectors.toList());
            existing.setFotos(fotos);
        }

        recetaRepository.save(existing);
    }

    // Utilitario para extraer extension de URL
    private String getExtension(String url) {
        int idx = url.lastIndexOf('.');
        return idx >= 0 ? url.substring(idx + 1) : null;
    }


    @Override
    public List<RecetaResumenResponse> buscarPorNombre(String nombre, String orden) {
        Specification<Receta> spec = Specification.where(conNombre(nombre));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToResumen)
                .sorted(getComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResumenResponse> buscarPorTipo(String tipo, String orden) {
        Specification<Receta> spec = Specification.where(conTipoDescripcion(tipo));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToResumen)
                .sorted(getComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResumenResponse> buscarPorIngrediente(String nombre, String orden) {
        Specification<Receta> spec = Specification.where(conIngrediente(nombre));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToResumen)
                .sorted(getComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResumenResponse> buscarSinIngrediente(String nombre, String orden) {
        Specification<Receta> spec = Specification.where(sinIngrediente(nombre));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToResumen)
                .sorted(getComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResumenResponse> buscarPorUsuario(String nombreUsuario, String orden) {
        Specification<Receta> spec = Specification.where(conNombreUsuario(nombreUsuario));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToResumen)
                .sorted(getComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaResumenResponse> buscarPorFiltros(RecetaFiltroRequest filtro) {
        Specification<Receta> spec = Specification.where(null);

        if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("nombreReceta")), "%" + filtro.getNombre().toLowerCase() + "%"));
        }

        if (filtro.getUsuario() != null && !filtro.getUsuario().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("usuario").get("nombre")), "%" + filtro.getUsuario().toLowerCase() + "%"));
        }

        if (filtro.getTipo() != null && !filtro.getTipo().isEmpty()) {
            try {
                Categoria cat = Categoria.valueOf(filtro.getTipo().toUpperCase());
                spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("tipo").get("descripcion"), cat));
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Tipo de receta inválido: " + filtro.getTipo());
            }
        }

        if (filtro.getIngredientesIncluidos() != null && !filtro.getIngredientesIncluidos().isEmpty()) {
            for (String ing : filtro.getIngredientesIncluidos()) {
                spec = spec.and((root, query, cb) -> {
                    var joinUtilizado = root.join("utilizados");
                    var joinIngrediente = joinUtilizado.join("ingrediente");
                    return cb.equal(cb.lower(joinIngrediente.get("nombre")), ing.toLowerCase());
                });
            }
        }

        if (filtro.getIngredientesExcluidos() != null && !filtro.getIngredientesExcluidos().isEmpty()) {
            for (String ing : filtro.getIngredientesExcluidos()) {
                spec = spec.and((root, query, cb) -> {
                    var joinUtilizado = root.join("utilizados");
                    var joinIngrediente = joinUtilizado.join("ingrediente");
                    return cb.notEqual(cb.lower(joinIngrediente.get("nombre")), ing.toLowerCase());
                });
            }
        }

        Sort sort = Sort.unsorted();
        if ("nombre_asc".equalsIgnoreCase(filtro.getOrden())) {
            sort = Sort.by("nombreReceta").ascending();
        } else if ("nombre_desc".equalsIgnoreCase(filtro.getOrden())) {
            sort = Sort.by("nombreReceta").descending();
        } else if ("fecha".equalsIgnoreCase(filtro.getOrden())) {
            sort = Sort.by("fechaCreacion").descending();
        }

        List<Receta> recetas = recetaRepository.findAll(spec, sort);
        return recetas.stream()
                .map(this::mapToResumen)
                .collect(Collectors.toList());
    }



    private RecetaResumenResponse mapToResumen(Receta receta) {
        RecetaResumenResponse r = new RecetaResumenResponse();
        r.setIdReceta(receta.getIdReceta());
        r.setNombre(receta.getNombreReceta());
        r.setFotoPrincipal(receta.getFotoPrincipal());
        r.setCantidadPersonas(receta.getCantidadPersonas());
        r.setTipo(receta.getTipo().getDescripcion().name());
        r.setNombreUsuario(receta.getUsuario().getNombre());
        r.setFechaCreacion(receta.getFechaCreacion());
        double promedio = receta.getCalificaciones().stream()
                .mapToInt(Calificacion::getCalificacion).average().orElse(0);
        r.setPromedioCalificacion(promedio);
        return r;
    }

    private RecetaDetalleResponse mapToDetalle(Receta receta) {
    RecetaDetalleResponse r = new RecetaDetalleResponse();
    r.setIdReceta(receta.getIdReceta());
    r.setNombre(receta.getNombreReceta());
    r.setDescripcion(receta.getDescripcionReceta());
    r.setFotoPrincipal(receta.getFotoPrincipal());
    r.setCantidadPersonas(receta.getCantidadPersonas());
    r.setTipo(receta.getTipo().getDescripcion().name());
    r.setNombreUsuario(receta.getUsuario().getNombre());
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


    private Comparator<RecetaResumenResponse> getComparator(String orden) {
        if (orden == null) return Comparator.comparing(RecetaResumenResponse::getIdReceta);
        return switch (orden.toLowerCase()) {
            case "nombre" -> Comparator.comparing(RecetaResumenResponse::getNombre);
            case "calificacion" -> Comparator.comparing(RecetaResumenResponse::getPromedioCalificacion).reversed();
            case "fecha" -> Comparator.comparing(RecetaResumenResponse::getFechaCreacion).reversed(); // más nuevas primero
            case "usuario" -> Comparator.comparing(RecetaResumenResponse::getNombreUsuario);
            case "fecha_usuario" -> Comparator
                .comparing(RecetaResumenResponse::getFechaCreacion).reversed()
                .thenComparing(RecetaResumenResponse::getNombreUsuario);
            default -> Comparator.comparing(RecetaResumenResponse::getIdReceta);
        };
    }

}