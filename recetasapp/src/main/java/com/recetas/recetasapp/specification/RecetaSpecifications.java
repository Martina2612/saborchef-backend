package com.recetas.recetasapp.specification;

import com.recetas.recetasapp.entity.Ingrediente;
import com.recetas.recetasapp.entity.Receta;
import com.recetas.recetasapp.entity.Utilizado;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.Locale;

public class RecetaSpecifications {
    public static Specification<Receta> conUsuario(Long idUsuario) {
        return (root, query, cb) -> cb.equal(root.get("usuario").get("idUsuario"), idUsuario);
    }

    public static Specification<Receta> conTipoId(Long idTipo) {
        return (root, query, cb) -> cb.equal(root.get("tipo").get("idTipo"), idTipo);
    }

    public static Specification<Receta> conNombre(String nombre) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("nombreReceta")), "%" + nombre.toLowerCase(Locale.ROOT) + "%");
    }

    public static Specification<Receta> conTipoDescripcion(String tipoDescripcion) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("tipo").get("descripcion")), tipoDescripcion.toLowerCase(Locale.ROOT));
    }

    public static Specification<Receta> conIngrediente(String nombreIngrediente) {
        return (root, query, cb) -> {
            Join<?, ?> utilizado = root.join("utilizados");
            Join<?, ?> ingrediente = utilizado.join("ingrediente");
            return cb.like(cb.lower(ingrediente.get("nombre")), "%" + nombreIngrediente.toLowerCase(Locale.ROOT) + "%");
        };
    }

     public static Specification<Receta> sinIngrediente(String nombreIngrediente) {
        return (root, query, cb) -> {
            // Creamos un subquery sobre Utilizado
            Subquery<Utilizado> sub = query.subquery(Utilizado.class);
            Root<Utilizado> u = sub.from(Utilizado.class);
            Join<Utilizado, Ingrediente> ing = u.join("ingrediente");

            // Seleccionamos cualquier utilizado donde:
            // 1) u.receta = root (correlación)
            // 2) el nombre del ingrediente coincide
            sub.select(u)
            .where(
                cb.equal(u.get("receta"), root),
                cb.like(cb.lower(ing.get("nombre")),
                        "%" + nombreIngrediente.toLowerCase(Locale.ROOT) + "%")
            );

            // Devolvemos recipes donde NO existe ese utilizado
            return cb.not(cb.exists(sub));
    };
}

    public static Specification<Receta> conDuracionMaxima(Integer minutosMax) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("duracion"), minutosMax);
    }

    public static Specification<Receta> conFechaCreacionDesde(LocalDateTime desde) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaCreacion"), desde);
    }

    public static Specification<Receta> conNombreUsuario(String nombreUsuario) {
    return (root, query, builder) ->
        builder.like(
            builder.lower(root.get("usuario").get("nombre")),
            "%" + nombreUsuario.toLowerCase() + "%"
        );
}
}