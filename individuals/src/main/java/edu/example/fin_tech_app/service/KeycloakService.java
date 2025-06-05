package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.config.KeycloakAdminProps;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import edu.example.fin_tech_app.dto.response.UserInfoResponse;
import edu.example.fin_tech_app.exception.KeycloakException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class KeycloakService {

  private static final String TOKEN_ENDPOINT = "/realms/%s/protocol/openid-connect/token";
  private static final String USER_ENDPOINT = "/admin/realms/{realm}/users/{id}";

  private final Keycloak keycloakAdminClient;
  private final WebClient webClient;
  private final KeycloakAdminProps keycloakAdminProps;

  @Value("${keycloak.fintech.realm}")
  private String realm;

  public boolean createUser(RegistrationRequest registrationRequest) {
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(registrationRequest.email());
    userRepresentation.setEmail(registrationRequest.email());
    userRepresentation.setEnabled(true);
    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
    credentialRepresentation.setTemporary(false);
    credentialRepresentation.setValue(registrationRequest.password());
    userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

    try (Response response = keycloakAdminClient.realm(realm)
        .users()
        .create(userRepresentation)) {
      if (response != null && response.getStatus() != HttpStatus.CREATED.value()) {
        throw new KeycloakException(response.getStatusInfo()
            .getReasonPhrase(), response.getStatus());
      }
    }
    return true;
  }

  public Mono<AuthResponse> getTokens() {
    String tokenUrl = String.format(TOKEN_ENDPOINT, keycloakAdminProps.getRealm());

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", keycloakAdminProps.getClientId());
    formData.add("username", keycloakAdminProps.getUsername());
    formData.add("password", keycloakAdminProps.getPassword());
    formData.add("grant_type", keycloakAdminProps.getGrantType());

    return webClient.post()
        .uri(tokenUrl)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new KeycloakException("Unnable to get access_token: " + errorBody,
                    clientResponse.statusCode()
                        .value()))))
        .bodyToMono(AuthResponse.class);
  }

  public Mono<AuthResponse> refreshToken(String refreshToken) {
    String tokenUrl = String.format(TOKEN_ENDPOINT, keycloakAdminProps.getRealm());

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("client_id", keycloakAdminProps.getClientId());
    formData.add("grant_type", "refresh_token");
    formData.add("refresh_token", refreshToken);

    return webClient.post()
        .uri(tokenUrl)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new KeycloakException("Unnable to refresh token: " + errorBody,
                    clientResponse.statusCode()
                        .value()))))
        .bodyToMono(AuthResponse.class);
  }

  public Mono<UserInfoResponse> getUserInfo(String authorization) {
    String userInfoUrl = String.format(USER_ENDPOINT, keycloakAdminProps.getRealm());

    return webClient.post()
        .uri(userInfoUrl)
        .header(HttpHeaders.AUTHORIZATION, authorization)
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new KeycloakException("Unnable to refresh token: " + errorBody,
                    clientResponse.statusCode()
                        .value()))))
        .bodyToMono(UserInfoResponse.class);
  }
}
