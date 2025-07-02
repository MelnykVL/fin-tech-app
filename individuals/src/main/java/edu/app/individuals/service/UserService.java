package edu.app.individuals.service;

import edu.app.individuals.client.KeycloakClient;
import edu.app.individuals.dto.request.LoginRequest;
import edu.app.individuals.dto.request.RegistrationRequest;
import edu.app.individuals.dto.response.AuthResponse;
import edu.app.individuals.dto.response.UserInfoResponse;
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