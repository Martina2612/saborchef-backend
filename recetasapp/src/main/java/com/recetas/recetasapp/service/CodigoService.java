package com.recetas.recetasapp.service;

public interface CodigoService {
    void enviarCodigoRegistro(String mail);
    boolean verificarCodigo(String mail, String codigo);
}
