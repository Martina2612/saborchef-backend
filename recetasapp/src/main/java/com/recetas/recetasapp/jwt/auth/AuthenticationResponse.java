package com.recetas.recetasapp.jwt.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("role")
    private String role;

    @JsonProperty("email")
    private String email;
    
}