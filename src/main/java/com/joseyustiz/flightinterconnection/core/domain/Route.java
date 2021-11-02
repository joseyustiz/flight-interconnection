package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Route {
    AirportIataCode departureAirport;
    AirportIataCode arrivalAirport;
}