// service/impl/TipoRecetaServiceImpl.java
package com.recetas.recetasapp.service.serviceimpl;

import com.recetas.recetasapp.repository.TipoRecetaRepository;
import com.recetas.recetasapp.service.TipoRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoRecetaServiceImpl implements TipoRecetaService {

    @Autowired
    private TipoRecetaRepository tipoRecetaRepository;

   @Override
    public List<String> listarTipos() {
        return tipoRecetaRepository.findAll()
            .stream()
            .map(tipo -> tipo.getDescripcion().name())  // convierto enum a String
            .collect(Collectors.toList());
    }
}

