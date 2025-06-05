package edu.example.fin_tech_app.exception;

import lombok.Getter;

@Getter
public class KeycloakException extends RuntimeException {

  private int status;

  public KeycloakException(String message, int status) {
    super(message);
    this.status = status;
  }
}
