package edu.example.fin_tech_app.exception;

import lombok.Getter;

@Getter
public class KeycloakIntegrationException extends RuntimeException {

  private final int status;

  public KeycloakIntegrationException(String message, int status) {
    super(message);
    this.status = status;
  }
}
