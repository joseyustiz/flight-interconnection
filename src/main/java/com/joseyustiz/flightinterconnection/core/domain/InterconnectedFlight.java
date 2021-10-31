package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class InterconnectedFlight {
    int stops;
    List<FlightSchedule> legs;
}
