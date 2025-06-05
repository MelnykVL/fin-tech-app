package edu.example.fin_tech_app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "keycloak.admin")
public class KeycloakAdminProps {

  private String realm;
  private String clientId;
  private String username;
  private String password;
  private String grantType;
}
