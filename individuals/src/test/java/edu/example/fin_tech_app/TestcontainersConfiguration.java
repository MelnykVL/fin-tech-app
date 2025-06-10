package edu.example.fin_tech_app;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration(proxyBeanMethods = false)
@Testcontainers
public class TestcontainersConfiguration {

    private static final String KC_PG_USER_PASS = "keycloak";
    private static final String KC_PG_DB = "keycloak_db";
    private static final String POSTGRES_IMAGE = "postgres:17";
    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.2";
    private static final String REALM_IMPORT_FILE = "realm-config.json";
    private static final String REALM = "fintech-app";
    private static final String CLIENT_ID = "individuals";
    private static final String CLIENT_SECRET = "QF8rGFdWsF6xzdgvjrEnVhVAHGlpFqLl";

    private static final Network NETWORK = Network.newNetwork();

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName(KC_PG_DB)
            .withUsername(KC_PG_USER_PASS)
            .withPassword(KC_PG_USER_PASS)
            .withNetwork(NETWORK)
            .withNetworkAliases("postgres");

    @Container
    public static final KeycloakContainer KEYCLOAK = new KeycloakContainer(KEYCLOAK_IMAGE)
            .withEnv("KEYCLOAK_ADMIN", KC_PG_USER_PASS)
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", KC_PG_USER_PASS)
            .withEnv("KC_DB", "postgres")
            .withEnv("KC_DB_USERNAME", POSTGRES.getUsername())
            .withEnv("KC_DB_PASSWORD", POSTGRES.getPassword())
            .withEnv("KC_DB_URL_HOST", "postgres")
            .withEnv("KC_DB_URL_DATABASE", POSTGRES.getDatabaseName())
            .withNetwork(NETWORK)
            .dependsOn(POSTGRES)
            .withRealmImportFile(REALM_IMPORT_FILE);

    @DynamicPropertySource
    static void registry(DynamicPropertyRegistry registry, KeycloakContainer keycloak) {
        registry.add("keycloak.individuals.base-url", keycloak::getAuthServerUrl);
        registry.add("keycloak.individuals.realm", () -> REALM);
        registry.add("keycloak.individuals.client-id", () -> CLIENT_ID);
        registry.add("keycloak.individuals.client-secret", () -> CLIENT_SECRET);
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/" + REALM
        );
    }
}
