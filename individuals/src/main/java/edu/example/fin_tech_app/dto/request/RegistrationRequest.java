package edu.example.fin_tech_app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(@Email(message = "The email format must be correct.") @NotBlank(
    message = "The email must not be blank.") String email,
                                  @Size(min = 8, message = "Password must contain more than 8 characters.") @NotBlank(
                                      message = "The password must not be blank.") String password,
                                  @NotBlank(message = "The confirm_password must not be blank.") @JsonProperty(
                                      "confirm_password") String confirmPassword) {

  @AssertTrue(message = "The password and confirm_password must be the same.")
  private boolean isPasswordsMatch() {
    return password != null && password.equals(confirmPassword);
  }
}
