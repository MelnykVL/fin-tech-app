package edu.example.fin_tech_app.controller;

import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RefreshTokenRequest;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import edu.example.fin_tech_app.dto.response.UserInfoResponse;
import edu.example.fin_tech_app.service.TokenService;
import edu.example.fin_tech_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
