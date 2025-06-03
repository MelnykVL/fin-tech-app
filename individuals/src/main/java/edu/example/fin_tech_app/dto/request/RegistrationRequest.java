package edu.example.fin_tech_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(@Email @NotBlank String email, @Size(min = 8) String password,
                                  @NotBlank String confirm_password) { }
