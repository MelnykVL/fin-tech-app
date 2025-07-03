package edu.app.individuals.controller;

import edu.app.individuals.config.ContainersConfig;
import edu.app.individuals.dto.request.LoginRequest;
import edu.app.individuals.dto.request.RegistrationRequest;
import edu.app.individuals.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "spring.main.web-application-type=reactive")
@ImportTestcontainers(ContainersConfig.class)
class AuthControllerIT {

  @LocalServerPort
  private int port;

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void registration() {
    RegistrationRequest registrationRequest =
        new RegistrationRequest("integration@test.com", "password123", "password123");

    webTestClient.post()
        .uri("http://localhost:" + port + "/v1/auth/registration")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(registrationRequest)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(AuthResponse.class);
  }

  @Test
  void login() {
    LoginRequest loginRequest = new LoginRequest("integration@test.com", "password123");

    webTestClient.post()
        .uri("http://localhost:" + port + "/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(loginRequest)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AuthResponse.class);
  }
}