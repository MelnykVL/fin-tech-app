package edu.example.fin_tech_app.exception;

import edu.example.fin_tech_app.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.stream.Collectors;

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

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<String>> handleGenericException(Exception ex, ServerWebExchange exchange) {
    log.error("An unexpected error occurred processing request {}: {}", exchange.getRequest()
        .getPath()
        .value(), ex.getMessage(), ex);
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ex.getMessage()));
  }
}