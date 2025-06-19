package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.client.KeycloakClient;
import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import edu.example.fin_tech_app.dto.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

  private final KeycloakClient keycloakClient;
  private final TokenService tokenService;

  public Mono<AuthResponse> register(RegistrationRequest registrationRequest) {
    return keycloakClient.createUser(registrationRequest);
  }

  public Mono<AuthResponse> login(LoginRequest loginRequest) {
    return tokenService.getTokens(loginRequest);
  }

  public Mono<UserInfoResponse> getUserInfo(String accessToken) {
    return keycloakClient.getUserInfo(accessToken);
  }
}