# Issue: https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/13475

This project sets up OpenTelemetry in the same way as: https://github.com/open-telemetry/opentelemetry-java-examples/tree/main/javaagent
Follow that projects README for instructions on how to run this repo.

I mixed the above repo with the reactive spring starter: https://spring.io/guides/gs/reactive-rest-service#initial

To see that there is no TRACE_CONTEXT_KEY ever set in the application context, run this application and 
set a breakpoint in the Controller class line 43 where there is a `Mono.deferContextual`.  As a sanity check, 
you can also breakpoint `TelemetryProducingWebFilter` and validate that the filter is being executed on each request.