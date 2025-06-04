package com.recetas.recetasapp.dto.response;

public class VerifyCodeResponse {
    private boolean success;
    private String message;

    public VerifyCodeResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
