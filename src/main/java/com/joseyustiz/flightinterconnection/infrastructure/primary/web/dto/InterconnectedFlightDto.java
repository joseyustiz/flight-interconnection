package com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterconnectedFlightDto {
    private int stops;
    private List<FlightScheduleDto> legs;
}
