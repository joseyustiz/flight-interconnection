package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Builder
@Value
public class Route implements Serializable {
    AirportIataCode departureAirport;
    AirportIataCode arrivalAirport;
}
