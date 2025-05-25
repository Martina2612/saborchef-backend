// controller/TipoRecetaController.java
package com.recetas.recetasapp.controller;

import com.recetas.recetasapp.service.TipoRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-receta")
public class TipoRecetaController {

    @Autowired
    private TipoRecetaService tipoRecetaService;

    @GetMapping
    public ResponseEntity<List<String>> listarTipos() {
        return ResponseEntity.ok(tipoRecetaService.listarTipos());
    }
}

