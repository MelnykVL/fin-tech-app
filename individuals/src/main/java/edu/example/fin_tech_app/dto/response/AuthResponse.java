package edu.example.fin_tech_app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(@JsonProperty("access_token") String accessToken, @JsonProperty("expires_in") Long expiresIn,
                           @JsonProperty("refresh_token") String refreshToken,
                           @JsonProperty("token_type") String tokenType) { }
