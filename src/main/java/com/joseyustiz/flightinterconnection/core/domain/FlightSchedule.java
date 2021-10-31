package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FlightSchedule {
    AirportIataCode departureAirport;
    AirportIataCode arrivalAirport;
    FlightDateTime departureDateTime;
    FlightDateTime  arrivalDateTime;
}
