package com.joseyustiz.flightinterconnection.core.port.secondary;

import com.joseyustiz.flightinterconnection.core.domain.Airline;
import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.Route;

import java.util.Set;

public interface RoutePort {

//    Flux<Route> getRoutesByConnectingAirportAndOperator(AirportIataCode connectingAirport, Airline operator);
    Set<Route> getRoutesByConnectingAirportAndOperatorAsList(AirportIataCode connectingAirport, Airline operator);
}
