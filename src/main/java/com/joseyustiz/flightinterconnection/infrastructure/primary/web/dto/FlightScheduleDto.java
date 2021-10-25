package com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightScheduleDto {
    String departureAirport;
    String arrivalAirport;
    LocalDateTime departureDateTime;
    LocalDateTime  arrivalDateTime;
}
