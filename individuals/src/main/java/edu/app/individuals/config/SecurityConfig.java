package edu.app.individuals.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  private static final int REFRESH_BEFORE_EXPIRATION_IN_SECONDS = 30;

  @Bean
  SecurityWebFilterChain springSecurity(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .authorizeExchange(exchange -> exchange.pathMatchers("/v1/auth/**")
            .permitAll()
            .pathMatchers("/v1/auth/me")
            .authenticated()
            .anyExchange()
            .denyAll())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .build();
  }

  @Bean
  ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager(
      ReactiveClientRegistrationRepository reactiveClientRegistrationRepository,
      ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService) {

    AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager clientManager =
        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(reactiveClientRegistrationRepository,
            reactiveOAuth2AuthorizedClientService);

    ClientCredentialsReactiveOAuth2AuthorizedClientProvider provider =
        new ClientCredentialsReactiveOAuth2AuthorizedClientProvider();
    provider.setClockSkew(Duration.ofSeconds(REFRESH_BEFORE_EXPIRATION_IN_SECONDS));


    clientManager.setAuthorizedClientProvider(provider);
    return clientManager;
  }
}
