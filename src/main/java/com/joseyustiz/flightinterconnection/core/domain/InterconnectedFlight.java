package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Builder
@Value
public class InterconnectedFlight implements Serializable {
    int stops;
    List<FlightSchedule> legs;
}
