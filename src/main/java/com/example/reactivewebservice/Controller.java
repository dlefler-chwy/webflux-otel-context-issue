package com.example.reactivewebservice;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
  private final AttributeKey<String> ATTR_METHOD = AttributeKey.stringKey("method");

  private final Random random = new Random();
  private final Tracer tracer;
  private final LongHistogram doWorkHistogram;

  @Autowired
  Controller(OpenTelemetry openTelemetry) {
    tracer = openTelemetry.getTracer(ReactiveWebServiceApplication.class.getName());
    Meter meter = openTelemetry.getMeter(ReactiveWebServiceApplication.class.getName());
    doWorkHistogram = meter.histogramBuilder("do-work").ofLongs().build();
  }

  @GetMapping("/ping")
  public Mono<String> ping(final ServerWebExchange exchange) throws InterruptedException {
    int sleepTime = random.nextInt(200);
    doWork(sleepTime);
    doWorkHistogram.record(sleepTime, Attributes.of(ATTR_METHOD, "ping"));
    return Mono.just("pong").flatMap(m -> Mono.deferContextual(ctx -> {
      return Mono.just(ctx);
    })).then(Mono.just("pong"));
  }

  private void doWork(int sleepTime) throws InterruptedException {
    Span span = tracer.spanBuilder("doWork").startSpan();
    try (Scope ignored = span.makeCurrent()) {
      Thread.sleep(sleepTime);
      LOGGER.info("A sample log message!");
    } finally {
      span.end();
    }
  }
}
