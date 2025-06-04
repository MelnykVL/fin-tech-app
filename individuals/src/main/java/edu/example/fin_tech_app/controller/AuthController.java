package edu.example.fin_tech_app.controller;

import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.TokenResponse;
import edu.example.fin_tech_app.service.TokenService;
import edu.example.fin_tech_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController("/v1/auth")
public class AuthController {

  private final TokenService tokenService;
  private final UserService userService;

  public AuthController(TokenService tokenService, UserService userService) {
    this.tokenService = tokenService;
    this.userService = userService;
  }

  @PostMapping("/registration")
  public Mono<ResponseEntity<TokenResponse>> registration(
      @Valid @RequestBody Mono<RegistrationRequest> registrationRequest) {
    return registrationRequest.flatMap(userService::register)
        .map(tr -> ResponseEntity.status(HttpStatus.CREATED)
            .body(tr));
  }

  @PostMapping("/login")
  public Mono<ResponseEntity<TokenResponse>> login(@Valid @RequestBody Mono<LoginRequest> loginRequest) {
    return loginRequest.flatMap(userService::login)
        .map(tr -> ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(tr));
  }
}
