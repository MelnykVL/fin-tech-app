package edu.app.individuals.exception;

import edu.app.individuals.dto.response.ErrorResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidationExceptions(WebExchangeBindException ex) {
    log.warn("Validation error occurred: {}", ex.getMessage(), ex);
    String errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(" "));

    ErrorResponse errorResponse = new ErrorResponse(errors, HttpStatus.BAD_REQUEST.value());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorResponse));
  }

  @ExceptionHandler(KeycloakIntegrationException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleKeycloakException(KeycloakIntegrationException ex) {
    String errorMessage = ex.getMessage();
    int statusCode = ex.getStatus();
    log.warn("Validation error occurred: {}", errorMessage, ex);
    if (statusCode == HttpStatus.CONFLICT.value()) {
      return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new ErrorResponse("User with this email already exists.", HttpStatus.CONFLICT.value())));
    }
    return Mono.just(ResponseEntity.status(statusCode)
        .body(new ErrorResponse(errorMessage, statusCode)));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
    log.error("An unexpected error occurred processing request {}: {}", exchange.getRequest()
        .getPath()
        .value(), ex.getMessage(), ex);
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
  }
}