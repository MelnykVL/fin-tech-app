package edu.example.fin_tech_app.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

  @Value("${keycloak.server.base-url}")
  private String baseUrl;

  @Bean
  public Keycloak keycloakAdminClient(KeycloakAdminProps keycloakAdminProps) {
    return KeycloakBuilder.builder()
        .serverUrl(baseUrl)
        .realm(keycloakAdminProps.getRealm())
        .clientId(keycloakAdminProps.getClientId())
        .username(keycloakAdminProps.getUsername())
        .password(keycloakAdminProps.getPassword())
        .grantType(keycloakAdminProps.getGrantType())
        .build();
  }
}