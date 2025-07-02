package edu.app.individuals.service;

import edu.app.individuals.client.KeycloakClient;
import edu.app.individuals.dto.request.LoginRequest;
import edu.app.individuals.dto.request.RefreshTokenRequest;
import edu.app.individuals.dto.response.AuthResponse;
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
