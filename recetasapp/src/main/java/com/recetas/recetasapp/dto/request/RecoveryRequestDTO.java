package com.recetas.recetasapp.dto.request;

public class RecoveryRequestDTO {
    private String email;

    public RecoveryRequestDTO() { }

    public RecoveryRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}