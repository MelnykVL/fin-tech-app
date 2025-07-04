package edu.app.individuals.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@Email @NotBlank String email, @Size(min = 8) String password) {
}
