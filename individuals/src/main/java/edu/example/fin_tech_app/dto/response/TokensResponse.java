package edu.example.fin_tech_app.dto.response;

public record TokensResponse(String access_token, Long expiresIn, String refresh_token, String tokenType) { }
