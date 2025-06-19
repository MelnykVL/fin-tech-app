package edu.example.fin_tech_app.client;

import edu.example.fin_tech_app.config.KeycloakProps;
import edu.example.fin_tech_app.dto.request.KeycloakCredentialsRepresentationRequest;
import edu.example.fin_tech_app.dto.request.KeycloakUserRepresentationRequest;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import edu.example.fin_tech_app.dto.response.UserInfoResponse;
import edu.example.fin_tech_app.exception.KeycloakIntegrationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KeycloakClient {

  private static final String TOKEN_ENDPOINT = "/realms/{realm}/protocol/openid-connect/token";
  private static final String USER_ENDPOINT = "/admin/realms/{realm}/users";
  private static final String USERINFO_ENDPOINT = "/realms/{realm}/protocol/openid-connect/userinfo";
  private static final String DEFAULT_SCOPES = "openid";
  private static final String KEYCLOAK_CREDENTIALS_REPRESENTATION_TYPE = "password";

  private final WebClient keycloakWebClient;
  private final WebClient adminKeycloakWebClient;
  private final KeycloakProps keycloakProps;

  public Mono<AuthResponse> createUser(RegistrationRequest registrationRequest) {
    return this.createUserInKeycloak(registrationRequest)
        .flatMap(userEmail -> this.getTokens(registrationRequest.email(), registrationRequest.password()));
  }

  public Mono<AuthResponse> getTokens(String email, String password) {
    return this.getTokens(email, password, DEFAULT_SCOPES);
  }

  public Mono<AuthResponse> getTokens(String username, String password, String scope) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add(OAuth2ParameterNames.CLIENT_ID, keycloakProps.getClientId());
    formData.add(OAuth2ParameterNames.CLIENT_SECRET, keycloakProps.getClientSecret());
    formData.add(OAuth2ParameterNames.USERNAME, username);
    formData.add(OAuth2ParameterNames.PASSWORD, password);
    formData.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());
    formData.add(OAuth2ParameterNames.SCOPE, scope);

    return keycloakWebClient.post()
        .uri(uriBuilder -> uriBuilder.path(TOKEN_ENDPOINT).build(keycloakProps.getRealm()))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(
                    new KeycloakIntegrationException("Unnable to get access_token: " + errorMessage,
                        clientResponse.statusCode()
                            .value()))))
        .bodyToMono(AuthResponse.class);
  }

  public Mono<AuthResponse> refreshToken(String refreshToken) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add(OAuth2ParameterNames.CLIENT_ID, keycloakProps.getClientId());
    formData.add(OAuth2ParameterNames.CLIENT_SECRET, keycloakProps.getClientSecret());
    formData.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue());
    formData.add(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken);

    return keycloakWebClient.post()
        .uri(uriBuilder -> uriBuilder.path(TOKEN_ENDPOINT).build(keycloakProps.getRealm()))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
            clientResponse -> clientResponse.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(
                    new KeycloakIntegrationException("Unnable to refresh token: " + errorMessage,
                        clientResponse.statusCode()
                            .value()))))
        .bodyToMono(AuthResponse.class);
  }

  public Mono<UserInfoResponse> getUserInfo(String accessToken) {
    return keycloakWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(USERINFO_ENDPOINT).build(keycloakProps.getRealm()))
        .header(HttpHeaders.AUTHORIZATION, accessToken)
        .retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp ->
            resp.bodyToMono(String.class)
                .flatMap(errorMessage ->
                    Mono.just(
                        new KeycloakIntegrationException("Unnable to create user: " + errorMessage, resp.statusCode()
                            .value()))))
        .bodyToMono(UserInfoResponse.class);
  }

  private Mono<String> createUserInKeycloak(RegistrationRequest registrationRequest) {
    KeycloakCredentialsRepresentationRequest credentials =
        new KeycloakCredentialsRepresentationRequest(KEYCLOAK_CREDENTIALS_REPRESENTATION_TYPE,
            registrationRequest.password(), Boolean.FALSE);
    KeycloakUserRepresentationRequest user =
        new KeycloakUserRepresentationRequest(registrationRequest.email(), Boolean.TRUE, List.of("users"),
            List.of(credentials));

    return adminKeycloakWebClient.post()
        .uri(uriBuilder -> uriBuilder.path(USER_ENDPOINT).build(keycloakProps.getRealm()))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(user).retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp ->
            resp.bodyToMono(String.class)
                .flatMap(errorMessage ->
                    Mono.just(
                        new KeycloakIntegrationException("Unnable to create user: " + errorMessage, resp.statusCode()
                            .value()))))
        .toBodilessEntity().thenReturn(registrationRequest.email());
  }
}