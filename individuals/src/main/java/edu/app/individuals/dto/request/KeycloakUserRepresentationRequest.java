package edu.app.individuals.dto.request;

import java.util.List;

public record KeycloakUserRepresentationRequest(String email, Boolean enabled, List<String> groups,
                                                List<KeycloakCredentialsRepresentationRequest> credentials) {
}
