package edu.app.individuals.exception;

import lombok.Getter;

@Getter
public class KeycloakIntegrationException extends RuntimeException {

  private final int status;

  public KeycloakIntegrationException(String message, int status) {
    super(message);
    this.status = status;
  }
}
