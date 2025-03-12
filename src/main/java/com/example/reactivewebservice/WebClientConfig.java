package com.example.reactivewebservice;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.webflux.v5_3.SpringWebfluxClientTelemetry;
import io.opentelemetry.instrumentation.spring.webflux.v5_3.SpringWebfluxServerTelemetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebClientConfig {
  private final SpringWebfluxClientTelemetry webfluxClientTelemetry;
  private final SpringWebfluxServerTelemetry webfluxServerTelemetry;

  public WebClientConfig(OpenTelemetry openTelemetry) {
    this.webfluxClientTelemetry = SpringWebfluxClientTelemetry.builder(openTelemetry).build();
    this.webfluxServerTelemetry = SpringWebfluxServerTelemetry.builder(openTelemetry).build();
  }

  // Adds instrumentation to WebClients
  @Bean
  public WebClient.Builder webClient() {
    WebClient webClient = WebClient.create();
    return webClient.mutate().filters(webfluxClientTelemetry::addFilter);
  }

  // Adds instrumentation to Webflux server
  @Bean
  public WebFilter webFilter() {
    return webfluxServerTelemetry.createWebFilterAndRegisterReactorHook();
  }
}