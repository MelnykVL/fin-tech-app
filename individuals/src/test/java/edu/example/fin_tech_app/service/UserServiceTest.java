package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import edu.example.fin_tech_app.dto.response.UserInfoResponse;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

  private KeycloakClient keycloakClient;
  private TokenService tokenService;
  private UserService userService;

  @BeforeEach
  void setUp() {
    keycloakClient = Mockito.mock(KeycloakClient.class);
    tokenService = Mockito.mock(TokenService.class);
    userService = new UserService(keycloakClient, tokenService);
  }

  @Test
  void registerDelegatesToKeycloakClient() {
    RegistrationRequest request = new RegistrationRequest("test@test.com", "password123", "password123");
    AuthResponse response = new AuthResponse("token", 1L, "refresh", "Bearer");
    when(keycloakClient.createUser(request)).thenReturn(Mono.just(response));

    AuthResponse result = userService.register(request).block();

    assertThat(result).isEqualTo(response);
    verify(keycloakClient).createUser(request);
  }

  @Test
  void loginDelegatesToTokenService() {
    LoginRequest request = new LoginRequest("test@test.com", "pass");
    AuthResponse response = new AuthResponse("token", 1L, "refresh", "Bearer");
    when(tokenService.getTokens(request)).thenReturn(Mono.just(response));

    AuthResponse result = userService.login(request).block();

    assertThat(result).isEqualTo(response);
    verify(tokenService).getTokens(request);
  }

  @Test
  void getUserInfoDelegatesToKeycloakClient() {
    UserInfoResponse userInfo = new UserInfoResponse("1", "email", List.of("role"), new Timestamp(0));
    when(keycloakClient.getUserInfo("Bearer token")).thenReturn(Mono.just(userInfo));

    UserInfoResponse result = userService.getUserInfo("Bearer token").block();

    assertThat(result).isEqualTo(userInfo);
    verify(keycloakClient).getUserInfo("Bearer token");
  }
}
