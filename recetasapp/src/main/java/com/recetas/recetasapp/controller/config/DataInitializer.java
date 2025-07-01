package com.recetas.recetasapp.controller.config;

import com.recetas.recetasapp.entity.Categoria;
import com.recetas.recetasapp.entity.TipoReceta;
import com.recetas.recetasapp.repository.TipoRecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TipoRecetaRepository tipoRepo;

    @Override
    public void run(String... args) {
        for (Categoria c : Categoria.values()) {
            tipoRepo.findByDescripcion(c)
                .orElseGet(() -> tipoRepo.save(new TipoReceta(null, c)));
        }
    }
}
