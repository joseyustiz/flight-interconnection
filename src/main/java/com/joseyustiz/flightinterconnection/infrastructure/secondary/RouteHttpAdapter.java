package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.Airline;
import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.IntegrationException;
import com.joseyustiz.flightinterconnection.core.domain.Route;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteHttpAdapter implements RoutePort {
    private static final WebClient webClient = WebClient.builder().build();
    private final RouteMapper mapper;

    //    @Override
//    public Flux<Route> getRoutesByConnectingAirportAndOperator(AirportIataCode connectingAirport, Airline operator) {
//        return null;
//    }
    @Override
    public Set<Route> getRoutesByConnectingAirportAndOperatorAsList(AirportIataCode connectingAirport, Airline operator) {
        final Function<UriBuilder, URI> url = uriBuilder -> uriBuilder
                .path("locate/3/routes")
                .scheme("https")
                .host("services-api.ryanair.com")
                .queryParam("operator", operator.getValue())
                .build();
        return webClient.get().uri(url).retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new IntegrationException(String.format(
                        "Error calling url %s for operator %s. HttpResponse: %s",
                        url, operator.getValue(), clientResponse.statusCode()))))
                .bodyToFlux(RouteDto.class)
                .filter(routeDto -> filerByConnectingAirport(connectingAirport, routeDto))
                .map(mapper::toDomain)
                .collect(Collectors.toSet()).block(Duration.ofSeconds(3));
    }

    private boolean filerByConnectingAirport(AirportIataCode connectingAirport, RouteDto routeDto) {
        return connectingAirport == AirportIataCode.NULL_AIRPORT ? routeDto.getConnectingAirport() == null : routeDto.getConnectingAirport().equals( connectingAirport.getValue());
    }

    @Data
    public static class RouteDto {
        String airportFrom;
        String airportTo;
        String connectingAirport;
    }
}
