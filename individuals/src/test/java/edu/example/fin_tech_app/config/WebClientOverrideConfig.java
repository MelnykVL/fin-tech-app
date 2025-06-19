package edu.example.fin_tech_app.config;

import edu.example.fin_tech_app.TestcontainersConfiguration;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@TestConfiguration
public class WebClientOverrideConfig {

  @Bean
  @Primary
  public WebClient testKeycloakWebClient() {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .responseTimeout(Duration.ofSeconds(5))
        .doOnConnected(conn ->
            conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

    return WebClient.builder()
        .baseUrl(TestcontainersConfiguration.KEYCLOAK.getAuthServerUrl())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
  }
}
