package com.recetas.recetasapp.service.serviceimpl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.recetas.recetasapp.dto.request.IngredienteCantidadDTO;
import com.recetas.recetasapp.dto.request.RecetaCrearRequest;
import com.recetas.recetasapp.dto.request.RecetaFiltroRequest;
import com.recetas.recetasapp.dto.response.RecetaDetalleResponse;
import com.recetas.recetasapp.dto.response.RecetaEscaladaResponse;
import com.recetas.recetasapp.dto.response.RecetaResumenResponse;
import com.recetas.recetasapp.entity.*;
import com.recetas.recetasapp.exception.DuplicateResourceException;
import com.recetas.recetasapp.exception.ResourceNotFoundException;
import com.recetas.recetasapp.repository.IngredienteRepository;
import com.recetas.recetasapp.repository.RecetaEditadaRepository;
import com.recetas.recetasapp.repository.RecetaRepository;
import com.recetas.recetasapp.repository.TipoRecetaRepository;
import com.recetas.recetasapp.repository.UnidadRepository;
import com.recetas.recetasapp.repository.UsuarioRepository;
import com.recetas.recetasapp.repository.UtilizadoRepository;
import com.recetas.recetasapp.service.RecetaService;
import com.recetas.recetasapp.specification.RecetaSpecifications;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    @Autowired
    private UtilizadoRepository utilizadoRepository;
    @Autowired
    private RecetaEditadaRepository recetaGuardadaRepository;

    @Override
public List<RecetaDetalleResponse> obtenerUltimas3Recetas() {
    List<Receta> recetas = recetaRepository.findTop3ByHabilitadaTrueOrderByFechaCreacionDesc();
    return recetas.stream()
            .map(this::mapToDetalle)
            .toList();
}

    @Override
    public List<RecetaResumenResponse> listarRecetas(Long u, Long t, String o) {
        Specification<Receta> spec = Specification.where(conHabilitada());
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
    @Transactional
    public void eliminarReceta(Long id) {
        if (!recetaRepository.existsById(id))
            throw new ResourceNotFoundException("Receta no encontrada");
        recetaRepository.deleteById(id);
    }


    @Override
@Transactional
public void crearReceta(RecetaCrearRequest req) {
    var usuario = usuarioRepository.findById(req.getIdUsuario())
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    // Validar receta duplicada
    recetaRepository.findByUsuarioIdAndNombreRecetaIgnoreCase(usuario.getId(), req.getNombreReceta())
        .ifPresent(existing -> {
            throw new DuplicateResourceException(
                "El usuario con ID " + usuario.getId() + " ya tiene una receta llamada '" + req.getNombreReceta() + "'",
                existing.getIdReceta()
            );
        });

    // Validar tipo de receta
    Categoria cat;
    try {
        cat = Categoria.valueOf(req.getTipo().toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new ResourceNotFoundException("Categoría inválida: " + req.getTipo());
    }

    var tipo = tipoRecetaRepository.findByDescripcion(cat)
        .orElseGet(() -> tipoRecetaRepository.save(new TipoReceta(null, cat)));

    // Crear receta sin guardarla aún
    var receta = new Receta();
    receta.setUsuario(usuario);
    receta.setTipo(tipo);
    receta.setNombreReceta(req.getNombreReceta());
    receta.setDescripcionReceta(req.getDescripcionReceta());
    receta.setFotoPrincipal(req.getFotoPrincipal());
    receta.setPorciones(req.getPorciones());
    receta.setDuracion(req.getDuracion());
    receta.setCantidadPersonas(req.getPorciones());
    receta.setFechaCreacion(LocalDateTime.now());
    receta.setHabilitada(false); // o true si querés que ya esté visible

    // Ingredientes utilizados
    var utilizados = req.getIngredientes().stream().map(ic -> {
        var ingr = ingredienteRepository.findByNombreIgnoreCase(ic.getNombreIngrediente())
            .orElseGet(() -> ingredienteRepository.save(new Ingrediente(null, ic.getNombreIngrediente())));

        var unidadDesc = ic.getUnidad().toLowerCase(Locale.ROOT);
        var unidadesValidas = List.of("kg", "gr", "ml", "litros", "unid.");
        if (!unidadesValidas.contains(unidadDesc)) {
            throw new ResourceNotFoundException("Unidad inválida: " + ic.getUnidad());
        }

        var unidad = unidadRepository.findByDescripcionIgnoreCase(unidadDesc)
            .orElseGet(() -> unidadRepository.save(new Unidad(null, unidadDesc)));

        var u = new Utilizado();
        u.setIngrediente(ingr);
        u.setUnidad(unidad);
        u.setCantidad(ic.getCantidad());
        u.setObservaciones(ic.getObservaciones());
        u.setReceta(receta);
        return u;
    }).toList();
    receta.setUtilizados(utilizados);

    // Pasos y contenidos multimedia
    var pasos = req.getPasos().stream().map(pc -> {
        var paso = new Pasos();
        paso.setNroPaso(pc.getNroPaso());
        paso.setTexto(pc.getTexto());
        paso.setReceta(receta);

        var contenidos = pc.getContenidos().stream().map(mc -> {
            var m = new Multimedia();
            m.setTipoContenido(mc.getTipoContenido());
            m.setExtension(mc.getExtension());
            m.setUrlContenido(mc.getUrlContenido());
            m.setPaso(paso);
            return m;
        }).toList();

        paso.setContenidos(contenidos);
        return paso;
    }).toList();
    receta.setPasos(pasos);

    // Fotos adicionales
    if (req.getFotos() != null) {
        var fotos = req.getFotos().stream().map(fc -> {
            var foto = new Foto();
            foto.setUrlFoto(fc.getUrlFoto());
            foto.setExtension(getExtension(fc.getUrlFoto()));
            foto.setDescripcion(fc.getDescripcion());
            foto.setReceta(receta);
            return foto;
        }).toList();
        receta.setFotos(fotos);
    }

    // Guardar todo en una sola transacción
    recetaRepository.save(receta);
}




    @Override
    public void actualizarReceta(Long id, RecetaCrearRequest request) {
        Receta existing = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada"));

        // Actualiza campos según el request
        existing.setNombreReceta(request.getNombreReceta());
        existing.setDescripcionReceta(request.getDescripcionReceta());
        existing.setFotoPrincipal(request.getFotoPrincipal());
        existing.setPorciones(request.getPorciones());

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
    public List<RecetaDetalleResponse> buscarPorNombre(String nombre, String orden) {
        Specification<Receta> spec = Specification.where(conHabilitada()).and(conNombre(nombre));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToDetalle)
                .sorted(getDetalleComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaDetalleResponse> buscarPorTipo(String tipo, String orden) {
        Specification<Receta> spec = Specification.where(conHabilitada()).and(conTipoDescripcion(tipo));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToDetalle)
                .sorted(getDetalleComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaDetalleResponse> buscarPorIngrediente(String nombre, String orden) {
        Specification<Receta> spec = Specification.where(conIngrediente(nombre));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToDetalle)
                .sorted(getDetalleComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaDetalleResponse> buscarSinIngrediente(String nombre, String orden) {
        Specification<Receta> spec = Specification.where(sinIngrediente(nombre));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToDetalle)
                .sorted(getDetalleComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaDetalleResponse> buscarPorUsuario(String nombreUsuario, String orden) {
        Specification<Receta> spec = Specification.where(conNombreUsuario(nombreUsuario));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToDetalle)
                .sorted(getDetalleComparator(orden))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RecetaDetalleResponse> buscarPorUsuarioId(Long usuarioId, String orden) {
        Specification<Receta> spec = Specification.where(conUsuario(usuarioId));
        return recetaRepository.findAll(spec).stream()
                .map(this::mapToDetalle)
                .sorted(getDetalleComparator(orden))
                .collect(Collectors.toList());
    }

    @Override
public List<RecetaDetalleResponse> buscarPorFiltros(RecetaFiltroRequest filtro) {
    Specification<Receta> spec = Specification.where(conHabilitada());

    // Nombre libre
    if (filtro.getNombre() != null && !filtro.getNombre().isBlank()) {
        spec = spec.and((root, query, cb) ->
            cb.like(cb.lower(root.get("nombreReceta")),
                    "%" + filtro.getNombre().toLowerCase() + "%"));
    }

    // Usuarios (lista)
    if (filtro.getUsuario() != null && !filtro.getUsuario().isEmpty()) {
        // construyo un OR sobre cada nombre de usuario
        Specification<Receta> userSpec = null;
        for (String usr : filtro.getUsuario()) {
            String lower = usr.toLowerCase();
            Specification<Receta> thisOne = (root, query, cb) ->
                cb.like(cb.lower(root.get("usuario").get("nombre")), "%" + lower + "%");
            userSpec = (userSpec == null) ? thisOne : userSpec.or(thisOne);
        }
        spec = spec.and(userSpec);
    }

    // Tipos (lista)
    if (filtro.getTipo() != null && !filtro.getTipo().isEmpty()) {
        Specification<Receta> typeSpec = null;
        for (String t : filtro.getTipo()) {
            try {
                Categoria cat = Categoria.valueOf(t.toUpperCase(Locale.ROOT));
                Specification<Receta> thisOne = (root, query, cb) ->
                    cb.equal(root.get("tipo").get("descripcion"), cat);
                typeSpec = (typeSpec == null) ? thisOne : typeSpec.or(thisOne);
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Tipo de receta inválido: " + t);
            }
        }
        spec = spec.and(typeSpec);
    }

    // Ingredientes incluidos
    if (filtro.getIngredientesIncluidos() != null && !filtro.getIngredientesIncluidos().isEmpty()) {
        for (String ing : filtro.getIngredientesIncluidos()) {
            String lower = ing.toLowerCase();
            spec = spec.and((root, query, cb) -> {
                var joinU = root.join("utilizados");
                var joinI = joinU.join("ingrediente");
                return cb.equal(cb.lower(joinI.get("nombre")), lower);
            });
        }
    }

    // Ingredientes excluidos
    if (filtro.getIngredientesExcluidos() != null && !filtro.getIngredientesExcluidos().isEmpty()) {
        for (String ing : filtro.getIngredientesExcluidos()) {
            String lower = ing.toLowerCase();
            spec = spec.and((root, query, cb) -> {
                var joinU = root.join("utilizados");
                var joinI = joinU.join("ingrediente");
                return cb.notEqual(cb.lower(joinI.get("nombre")), lower);
            });
        }
    }

    // Orden
    Sort sort = Sort.unsorted();
    if ("nombre_asc".equalsIgnoreCase(filtro.getOrden())) {
        sort = Sort.by("nombreReceta").ascending();
    } else if ("nombre_desc".equalsIgnoreCase(filtro.getOrden())) {
        sort = Sort.by("nombreReceta").descending();
    } else if ("fecha".equalsIgnoreCase(filtro.getOrden())) {
        sort = Sort.by("fechaCreacion").descending();
    } else if ("usuario".equalsIgnoreCase(filtro.getOrden())) {
        sort = Sort.by("usuario.nombre").ascending();
    }

    List<Receta> recetas = recetaRepository.findAll(spec, sort);
    return recetas.stream()
                  .map(this::mapToDetalle)
                  .collect(Collectors.toList());
}


    private RecetaResumenResponse mapToResumen(Receta receta) {
        RecetaResumenResponse r = new RecetaResumenResponse();
        r.setIdReceta(receta.getIdReceta());
        r.setNombre(receta.getNombreReceta());
        r.setFotoPrincipal(receta.getFotoPrincipal());
        r.setCantidadPersonas(receta.getPorciones());
        r.setTipo(receta.getTipo().getDescripcion().name());
        r.setNombreUsuario(receta.getUsuario().getUsername());
        r.setDuracion(receta.getDuracion());
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
    r.setPorciones(receta.getPorciones());
    r.setDuracion(receta.getDuracion());
    r.setTipo(receta.getTipo().getDescripcion().name());
    r.setNombreUsuario(receta.getUsuario().getUsername());
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

    @Override
    public List<RecetaDetalleResponse> obtenerUltimasRecetas() {
        return recetaRepository
        .findAll(
            Specification.where(conHabilitada()),
            PageRequest.of(0, 12, Sort.by("fechaCreacion").descending())
        )
        .stream()
        .map(this::mapToDetalle)
        .toList();
    }

    /**
     * Factoriza la receta con un factor fijo (por ejemplo 0.5, 2.0, etc.).
     */
    @Override
    @Transactional
    public RecetaEscaladaResponse escalarRecetaPorFactor(Long idReceta, Double factor) throws Exception {
        if (factor == null || factor <= 0) {
            throw new IllegalArgumentException("El factor de escalado debe ser un número positivo.");
        }
        Optional<Receta> opt = recetaRepository.findById(idReceta);
        if (opt.isEmpty()) {
            throw new Exception("No se encontró la receta con id " + idReceta);
        }
        Receta receta = opt.get();
        return generarRecetaEscalada(receta, factor);
    }

    /**
     * Cálculo de factor a partir de porciones deseadas: porcDeseada / porcionesOriginal.
     */
    @Override
    @Transactional
    public RecetaEscaladaResponse escalarRecetaPorPorciones(Long idReceta, Integer porcionesDeseadas) throws Exception {
        if (porcionesDeseadas == null || porcionesDeseadas <= 0) {
            throw new IllegalArgumentException("Las porciones deseadas deben ser un entero positivo.");
        }
        Optional<Receta> opt = recetaRepository.findById(idReceta);
        if (opt.isEmpty()) {
            throw new Exception("No se encontró la receta con id " + idReceta);
        }
        Receta receta = opt.get();
        Integer porcionesOriginal = receta.getPorciones();
        if (porcionesOriginal == null || porcionesOriginal <= 0) {
            throw new IllegalStateException("La receta original no tiene porciones válidas.");
        }
        Double factor = porcionesDeseadas.doubleValue() / porcionesOriginal.doubleValue();
        return generarRecetaEscalada(receta, factor);
    }

    /**
     * Cálculo de factor a partir de la cantidad de un ingrediente concreto:
     * factor = nuevaCantidadIngresada / cantidadOriginalDelIngredienteElegido.
     */
    @Override
    @Transactional
    public RecetaEscaladaResponse escalarRecetaPorIngrediente(Long idReceta, Long ingredienteId, Double nuevaCantidad) throws Exception {
        if (ingredienteId == null || nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new IllegalArgumentException("Debe indicar un ingrediente y una cantidad positiva.");
        }
        Optional<Receta> opt = recetaRepository.findById(idReceta);
        if (opt.isEmpty()) {
            throw new Exception("No se encontró la receta con id " + idReceta);
        }
        Receta receta = opt.get();

        // Buscamos el Utilizado que corresponda al ingredienteId
        List<Utilizado> listaUtilizados = utilizadoRepository.findAllByRecetaIdReceta(idReceta);
        Optional<Utilizado> utilOpt = listaUtilizados.stream()
                .filter(u -> u.getIngrediente().getIdIngrediente().equals(ingredienteId))
                .findFirst();

        if (utilOpt.isEmpty()) {
            throw new Exception("El ingrediente con id " + ingredienteId + " no está en la receta " + idReceta);
        }
        Utilizado uOrigen = utilOpt.get();
        Double cantidadOriginal = uOrigen.getCantidad();
        if (cantidadOriginal == null || cantidadOriginal <= 0) {
            throw new IllegalStateException("Cantidad original del ingrediente inválida.");
        }

        Double factor = nuevaCantidad / cantidadOriginal;
        return generarRecetaEscalada(receta, factor);
    }

    /**
     * Genera el DTO RecetaEscaladaResponse a partir de la entidad Receta y un factor dado.
     */
    private RecetaEscaladaResponse generarRecetaEscalada(Receta receta, Double factor) {
        // 1) Calcular nueva cantidad de porciones
        Integer porcionesOriginal = receta.getPorciones();
        Integer porcionesEscaladas = (porcionesOriginal != null)
                ? (int) Math.round(porcionesOriginal * factor)
                : null;

        // 2) Obtener todos los utilizados y multiplicar cantidades
        List<Utilizado> listaUtilizados = utilizadoRepository.findAllByRecetaIdReceta(receta.getIdReceta());

        List<IngredienteCantidadDTO> ingredientesDTO = listaUtilizados.stream().map(u -> {
            Ingrediente ingr = u.getIngrediente();
            Unidad unidad = u.getUnidad();

            Double cantidadOriginal = u.getCantidad();
            Double cantidadEscalada = null;
            if (cantidadOriginal != null) {
                cantidadEscalada = cantidadOriginal * factor;
            }
            return new IngredienteCantidadDTO(
                    ingr.getIdIngrediente(),
                    ingr.getNombre(),
                    cantidadEscalada,
                    (unidad != null ? unidad.getDescripcion() : ""),
                    u.getObservaciones()
            );
        }).collect(Collectors.toList());

        // 3) Armar el DTO final
        RecetaEscaladaResponse resp = new RecetaEscaladaResponse();
        resp.setIdRecetaOriginal(receta.getIdReceta());
        resp.setNombreReceta(receta.getNombreReceta());
        resp.setNombreUsuario(receta.getUsuario().getNombre()); 
        resp.setTipoReceta(receta.getTipo().getDescripcion().name()); 
        resp.setDescripcionReceta(receta.getDescripcionReceta());
        resp.setPorcionesOriginal(porcionesOriginal);
        resp.setPorcionesEscaladas(porcionesEscaladas);
        resp.setFactorEscalado(factor);
        resp.setIngredientes(ingredientesDTO);

        return resp;
    }

    // ______________________________________________
    // 3) LÓGICA DE GUARDADO DE RECETAS ESCALADAS (HASTA 10 POR USUARIO)
    // ______________________________________________

    @Override
    @Transactional
    public void guardarRecetaEscalada(Long idReceta, Usuario usuario, Double factor) throws Exception {
        if (usuario == null) {
            throw new IllegalArgumentException("Se debe indicar el usuario que guarda la receta.");
        }
        Optional<Receta> optReceta = recetaRepository.findById(idReceta);
        if (optReceta.isEmpty()) {
            throw new Exception("No se encontró la receta con id " + idReceta);
        }

        // 1) Verificar cuántas recetas guardadas tiene el usuario
        Long contador = recetaGuardadaRepository.countByUsuario(usuario);
        if (contador >= 10) {
            throw new Exception("Ya alcanzaste el límite de 10 recetas guardadas. Borra alguna antes de guardar otra.");
        }

        // 2) Construir la entidad RecetaGuardada y persistir
        RecetaEditada guardada = new RecetaEditada();
        guardada.setUsuario(usuario);
        guardada.setRecetaOriginal(optReceta.get());
        guardada.setFactorEscalado(factor);
        guardada.setFechaGuardado(LocalDateTime.now());

        recetaGuardadaRepository.save(guardada);
    }

    @Override
    @Transactional
    public List<RecetaEscaladaResponse> listarRecetasEscaladasGuardadas(Usuario usuario) {
        if (usuario == null) {
            return Collections.emptyList();
        }
        List<RecetaEditada> lista = recetaGuardadaRepository.findAllByUsuario(usuario);
        // Por cada guardada, recupero datos y genero el DTO (con el factor almacenado).
        return lista.stream()
                .map(rg -> {
                    Receta r = rg.getRecetaOriginal();
                    Double factor = rg.getFactorEscalado();
                    // Reuso la función para generar la receta escalada:
                    return generarRecetaEscalada(r, factor);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarRecetaGuardada(Long idRecetaGuardada, Usuario usuario) throws Exception {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no válido.");
        }
        Optional<RecetaEditada> opt = recetaGuardadaRepository.findById(idRecetaGuardada);
        if (opt.isEmpty()) {
            throw new Exception("No se encontró la receta guardada con id " + idRecetaGuardada);
        }
        RecetaEditada rg = opt.get();
        if (!rg.getUsuario().getId().equals(usuario.getId())) {
            throw new Exception("No tienes permiso para eliminar esta receta guardada.");
        }
        recetaGuardadaRepository.delete(rg);
    }

    private Comparator<RecetaDetalleResponse> getDetalleComparator(String orden) {
    if (orden == null) return Comparator.comparing(RecetaDetalleResponse::getIdReceta);
    return switch (orden.toLowerCase()) {
        case "nombre" -> Comparator.comparing(RecetaDetalleResponse::getNombre);
        case "calificacion" -> Comparator.comparing(RecetaDetalleResponse::getPromedioCalificacion).reversed();
        case "fecha" -> Comparator.comparing(RecetaDetalleResponse::getFechaCreacion).reversed();
        case "usuario" -> Comparator.comparing(RecetaDetalleResponse::getNombreUsuario);
        case "fecha_usuario" -> Comparator
            .comparing(RecetaDetalleResponse::getFechaCreacion).reversed()
            .thenComparing(RecetaDetalleResponse::getNombreUsuario);
        default -> Comparator.comparing(RecetaDetalleResponse::getIdReceta);
    };
}


}