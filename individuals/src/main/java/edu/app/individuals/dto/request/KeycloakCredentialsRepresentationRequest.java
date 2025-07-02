package edu.app.individuals.dto.request;

public record KeycloakCredentialsRepresentationRequest(String type, String value, Boolean temporary) {
}
