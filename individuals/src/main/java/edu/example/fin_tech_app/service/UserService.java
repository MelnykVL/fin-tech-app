package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import edu.example.fin_tech_app.dto.response.UserInfoResponse;
import io.micrometer.observation.ObservationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class UserService {

  private final KeycloakService keycloakService;

  public Mono<AuthResponse> register(RegistrationRequest registrationRequest) {
    return Mono.fromCallable(() -> keycloakService.createUser(registrationRequest))
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(isCreated -> keycloakService.getTokens());
  }

  public Mono<AuthResponse> login(LoginRequest loginRequest) {
    return keycloakService.getTokens();
  }

  public Mono<UserInfoResponse> getUserInfo(String authorization) {
    return keycloakService.getUserInfo(authorization);
  }
}
