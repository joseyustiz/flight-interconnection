package com.joseyustiz.flightinterconnection.infrastructure.secondary.dto;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Value
public class FlightScheduleWeightedEdge extends DefaultWeightedEdge {
    LocalDateTime departureDateTime;
    LocalDateTime arrivalDateTime;

    public FlightScheduleWeightedEdge(LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
    }

    public AirportIataCode getDepartureAirport(){
        return (AirportIataCode) super.getSource();
    }

    public AirportIataCode getArrivalAirport(){
        return (AirportIataCode) super.getTarget();
    }

    @Override
    public String toString() {
        return "FlightScheduleWeightedEdge(" +
                "departureAirport=" + super.getSource() +
                ", departureDateTime=" + departureDateTime +
                ", arrivalAirport=" + super.getTarget() +
                ", arrivalDateTime=" + arrivalDateTime +
                ')';
    }
}
