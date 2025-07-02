package edu.app.individuals.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "keycloak.individuals")
public class KeycloakProps {

  private String realm;
  private String clientId;
  private String clientSecret;
}
