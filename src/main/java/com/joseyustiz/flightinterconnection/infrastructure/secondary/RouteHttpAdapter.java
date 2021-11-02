package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.Airline;
import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.Route;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class RouteHttpAdapter implements RoutePort {
//    @Override
//    public Flux<Route> getRoutesByConnectingAirportAndOperator(AirportIataCode connectingAirport, Airline operator) {
//        return null;
//    }
    @Override
    public Set<Route> getRoutesByConnectingAirportAndOperatorAsList(AirportIataCode connectingAirport, Airline operator) {
        return Collections.emptySet();
    }
}
