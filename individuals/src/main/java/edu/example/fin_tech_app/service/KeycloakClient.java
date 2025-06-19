package edu.example.fin_tech_app.service;

import edu.example.fin_tech_app.config.KeycloakProps;
import edu.example.fin_tech_app.dto.request.RegistrationRequest;
import edu.example.fin_tech_app.dto.response.AuthResponse;
import edu.example.fin_tech_app.dto.response.UserInfoResponse;
import edu.example.fin_tech_app.exception.KeycloakIntegrationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

  private final WebClient webClient;
  private final KeycloakProps keycloakProps;

  public Mono<AuthResponse> createUser(RegistrationRequest registrationRequest) {
    return getAdminAccessToken()
        .flatMap(authResponse -> this.createUserInKeycloak(registrationRequest, authResponse.accessToken()))
        .flatMap(userEmail -> this.getTokens(registrationRequest.email(), registrationRequest.password()));
  }

  public Mono<AuthResponse> getTokens(String email, String password) {
    return this.getTokens(email, password, "openid");
  }

  public Mono<AuthResponse> getTokens(String username, String password, String scope) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add(OAuth2ParameterNames.CLIENT_ID, keycloakProps.getClientId());
    formData.add(OAuth2ParameterNames.CLIENT_SECRET, keycloakProps.getClientSecret());
    formData.add(OAuth2ParameterNames.USERNAME, username);
    formData.add(OAuth2ParameterNames.PASSWORD, password);
    formData.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.PASSWORD.getValue());
    formData.add(OAuth2ParameterNames.SCOPE, scope);

    return webClient.post()
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

    return webClient.post()
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
    return webClient.get()
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

  private Mono<AuthResponse> getAdminAccessToken() {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add(OAuth2ParameterNames.CLIENT_ID, keycloakProps.getClientId());
    formData.add(OAuth2ParameterNames.CLIENT_SECRET, keycloakProps.getClientSecret());
    formData.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.CLIENT_CREDENTIALS.getValue());

    return webClient.post().uri(uriBuilder -> uriBuilder.path(TOKEN_ENDPOINT).build(keycloakProps.getRealm()))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formData)).retrieve()
        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp ->
            resp.bodyToMono(String.class)
                .flatMap(errorMessage ->
                    Mono.just(
                        new KeycloakIntegrationException("Unnabel to get admin token" + errorMessage, resp.statusCode()
                            .value()))))
        .bodyToMono(AuthResponse.class);
  }

  private Mono<String> createUserInKeycloak(RegistrationRequest registrationRequest, String adminAccessToken) {
    Map<String, Object> credentials = new LinkedHashMap<>();
    credentials.put("type", "password");
    credentials.put("value", registrationRequest.password());
    credentials.put("temporary", Boolean.FALSE);
    Map<String, Object> user = new LinkedHashMap<>();
    user.put("email", registrationRequest.email());
    user.put("enabled", Boolean.TRUE);
    user.put("groups", List.of("users"));
    user.put("credentials", List.of(credentials));

    return webClient.post()
        .uri(uriBuilder -> uriBuilder.path(USER_ENDPOINT).build(keycloakProps.getRealm()))
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
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
