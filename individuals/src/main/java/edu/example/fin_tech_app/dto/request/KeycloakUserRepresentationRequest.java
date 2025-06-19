package edu.example.fin_tech_app.dto.request;

import java.util.List;

public record KeycloakUserRepresentationRequest(String email, Boolean enabled, List<String> groups,
                                                List<KeycloakCredentialsRepresentationRequest> credentials) {
}
