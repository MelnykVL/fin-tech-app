package edu.example.fin_tech_app.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  private static final int CONNECT_TIMEOUT_IN_MILLIS = 5000;
  private static final int READ_TIMEOUT_IN_SECONDS = 10;
  private static final int WRITE_TIMEOUT_IN_SECONDS = 10;
  private static final int RESPONSE_TIMEOUT_IN_SECONDS = 5;

  @Bean
  public WebClient keycloakWebClient(@Value("${keycloak.individuals.base-url}") String baseUrl) {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_IN_MILLIS)
        .responseTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT_IN_SECONDS))
        .doOnConnected(conn ->
            conn.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)));

    return WebClient.builder()
        .baseUrl(baseUrl)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }

  @Bean
  public WebClient adminKeycloakWebClient(@Value("${keycloak.individuals.base-url}") String baseUrl,
      ReactiveOAuth2AuthorizedClientManager clientManager) {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_IN_MILLIS)
        .responseTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT_IN_SECONDS))
        .doOnConnected(conn ->
            conn.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)));

    ServerOAuth2AuthorizedClientExchangeFilterFunction auth2AuthorizedClientExchangeFilterFunction =
        new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientManager);

    auth2AuthorizedClientExchangeFilterFunction.setDefaultClientRegistrationId("keycloak-admin-client-registration");

    return WebClient.builder()
        .baseUrl(baseUrl)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .filter(auth2AuthorizedClientExchangeFilterFunction)
        .build();
  }
}
