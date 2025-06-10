package edu.example.fin_tech_app.controller;

import edu.example.fin_tech_app.TestcontainersConfiguration;
import edu.example.fin_tech_app.config.WebClientOverrideConfig;
import edu.example.fin_tech_app.dto.request.LoginRequest;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import({TestcontainersConfiguration.class, WebClientOverrideConfig.class})
@Testcontainers
class AuthControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void startContainers() {
        TestcontainersConfiguration.KEYCLOAK.start();
    }

    @Test
    void registration() {
        RegistrationRequest registrationRequest = new RegistrationRequest("integration@test.com", "password123", "password123");

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