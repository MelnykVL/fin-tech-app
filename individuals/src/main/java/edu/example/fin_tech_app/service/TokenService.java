package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RefreshTokenRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final KeycloakClient keycloakClient;

  public Mono<AuthResponse> getTokens(LoginRequest loginRequest) {
    return keycloakClient.getTokens(loginRequest.email(), loginRequest.password());
  }

  public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    return keycloakClient.refreshToken(refreshTokenRequest.refreshToken());
  }
}
