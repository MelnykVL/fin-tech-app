package edu.example.fin_tech_app.dto.response;

public record AuthResponse(String access_token, Long expiresIn, String refresh_token, String tokenType) { }
