package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class FlightSchedule implements Serializable {
    AirportIataCode departureAirport;
    AirportIataCode arrivalAirport;
    FlightDateTime departureDateTime;
    FlightDateTime  arrivalDateTime;
}
