package com.recetas.recetasapp.service.serviceimpl;

import com.recetas.recetasapp.service.CodigoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class CodigoServiceImpl implements CodigoService {

    private final Map<String, String> codigos = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void enviarCodigoRegistro(String mail) {
        String codigo = String.format("%06d", random.nextInt(1000000));
        codigos.put(mail, codigo);

        // Simulación: Mostrar código en consola
        System.out.println("Código enviado a " + mail + ": " + codigo);
    }

    @Override
    public boolean verificarCodigo(String mail, String codigo) {
        return codigos.containsKey(mail) && codigos.get(mail).equals(codigo);
    }
}
