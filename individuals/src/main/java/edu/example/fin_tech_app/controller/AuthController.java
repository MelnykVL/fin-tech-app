package edu.example.fin_tech_app.controller;

import edu.example.fin_tech_app.service.TokenService;
import edu.example.fin_tech_app.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  private final TokenService tokenService;
  private final UserService userService;

  public AuthController(TokenService tokenService, UserService userService) {
    this.tokenService = tokenService;
    this.userService = userService;
  }
}
