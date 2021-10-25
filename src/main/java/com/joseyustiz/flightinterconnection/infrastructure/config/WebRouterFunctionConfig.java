package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@RequiredArgsConstructor
public class WebRouterFunctionConfig {
    private final GetInterconnectedFlightHandler handler;
    @Bean
    public RouterFunction<ServerResponse> interconnectionRoutes() {

        return RouterFunctions.route()
                .path("/v1/flights/interconnections", builder -> builder
                        .GET("", accept(MediaType.APPLICATION_JSON), handler::getInterconnections))
                .build();
    }

}
