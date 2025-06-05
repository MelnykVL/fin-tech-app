package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.dto.request.RefreshTokenRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final KeycloakService keycloakService;

  public Mono<AuthResponse> getTokens() {
    return keycloakService.getTokens();
  }

  public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    return keycloakService.refreshToken(refreshTokenRequest.refreshToken());
  }
}
