package com.recetas.recetasapp.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String email;
    private String nuevaPassword;
}