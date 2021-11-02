package com.joseyustiz.flightinterconnection.core.port.secondary;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.dto.FlightScheduleWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CalculatePathPort {
    Flux<InterconnectedFlight> calculateInterconnectedFlights(GetInterconnectedFlightUseCase.Query query, DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge> routesGraph );
    List<InterconnectedFlight> calculateInterconnectedFlightsCollection(GetInterconnectedFlightUseCase.Query query, DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge> routesGraph );


}
