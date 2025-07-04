package edu.app.individuals.controller;

import edu.app.individuals.dto.request.LoginRequest;
import edu.app.individuals.dto.request.RefreshTokenRequest;
import edu.app.individuals.dto.request.RegistrationRequest;
import edu.app.individuals.dto.response.AuthResponse;
import edu.app.individuals.dto.response.UserInfoResponse;
import edu.app.individuals.service.TokenService;
import edu.app.individuals.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

  private final TokenService tokenService;
  private final UserService userService;

  public AuthController(TokenService tokenService, UserService userService) {
    this.tokenService = tokenService;
    this.userService = userService;
  }

  @PostMapping("/registration")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<ResponseEntity<AuthResponse>> registration(
      @Valid @RequestBody Mono<RegistrationRequest> registrationRequest) {
    return registrationRequest.flatMap(userService::register)
        .map(response -> ResponseEntity.status(HttpStatus.CREATED)
            .body(response));
  }

  @PostMapping("/login")
  public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody Mono<LoginRequest> loginRequest) {
    return loginRequest.flatMap(userService::login)
        .map(ar -> ResponseEntity.status(HttpStatus.OK)
            .body(ar));
  }

  @PostMapping("/refresh-token")
  public Mono<ResponseEntity<AuthResponse>> refreshToken(
      @Valid @RequestBody Mono<RefreshTokenRequest> refreshTokenRequest) {
    return refreshTokenRequest.flatMap(tokenService::refreshToken)
        .map(ar -> ResponseEntity.status(HttpStatus.OK)
            .body(ar));
  }

  @GetMapping("/me")
  public Mono<ResponseEntity<UserInfoResponse>> me(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    return userService.getUserInfo(authorization)
        .map(ar -> ResponseEntity.status(HttpStatus.OK)
            .body(ar));
  }
}
