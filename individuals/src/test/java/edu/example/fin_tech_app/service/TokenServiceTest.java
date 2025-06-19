package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.client.KeycloakClient;
import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RefreshTokenRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TokenServiceTest {
  private KeycloakClient keycloakClient;
  private TokenService tokenService;

  @BeforeEach
  void setUp() {
    keycloakClient = Mockito.mock(KeycloakClient.class);
    tokenService = new TokenService(keycloakClient);
  }

  @Test
  void getTokensDelegatesToKeycloakClient() {
    LoginRequest request = new LoginRequest("user@example.com", "password");
    AuthResponse response = new AuthResponse("token", 1L, "refresh", "Bearer");
    when(keycloakClient.getTokens(request.email(), request.password())).thenReturn(Mono.just(response));

    AuthResponse result = tokenService.getTokens(request).block();

    assertThat(result).isEqualTo(response);
    ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
    verify(keycloakClient).getTokens(emailCaptor.capture(), passwordCaptor.capture());
    assertThat(emailCaptor.getValue()).isEqualTo(request.email());
    assertThat(passwordCaptor.getValue()).isEqualTo(request.password());
  }

  @Test
  void refreshTokenDelegatesToKeycloakClient() {
    RefreshTokenRequest request = new RefreshTokenRequest("ref");
    AuthResponse response = new AuthResponse("token", 1L, "refresh", "Bearer");
    when(keycloakClient.refreshToken(request.refreshToken())).thenReturn(Mono.just(response));

    AuthResponse result = tokenService.refreshToken(request).block();

    assertThat(result).isEqualTo(response);
    verify(keycloakClient).refreshToken(request.refreshToken());
  }
}
