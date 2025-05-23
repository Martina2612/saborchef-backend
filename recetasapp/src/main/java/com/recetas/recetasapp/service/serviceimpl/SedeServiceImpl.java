package com.recetas.recetasapp.service.serviceimpl;

import com.recetas.recetasapp.entity.Sede;
import com.recetas.recetasapp.repository.SedeRepository;
import com.recetas.recetasapp.service.SedeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;

    public SedeServiceImpl(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    @Override
    public Sede guardarSede(Sede sede) {
        return sedeRepository.save(sede);
    }

    @Override
    public List<Sede> obtenerTodasLasSedes() {
        return sedeRepository.findAll();
    }

    @Override
    public Optional<Sede> obtenerSedePorId(Long id) {
        return sedeRepository.findById(id);
    }
}
