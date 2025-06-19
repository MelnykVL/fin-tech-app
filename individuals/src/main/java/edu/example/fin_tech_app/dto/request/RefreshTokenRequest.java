package edu.example.fin_tech_app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "The refresh_token must be not blank.") @JsonProperty("refresh_token") String refreshToken) {
}
