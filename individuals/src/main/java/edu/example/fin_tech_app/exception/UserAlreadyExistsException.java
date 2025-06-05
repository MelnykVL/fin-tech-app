package edu.example.fin_tech_app.exception;

public class UserAlreadyExistsException extends RuntimeException {

  private int status;

  public UserAlreadyExistsException(String message, int status) {
    super(message);
    this.status = status;
  }
}
