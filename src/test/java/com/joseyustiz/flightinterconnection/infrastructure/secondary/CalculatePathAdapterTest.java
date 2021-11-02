package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.dto.FlightScheduleWeightedEdge;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatePathAdapterTest {
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2021, 11, 1, 5, 0);
    private static final String AIRPORT_DUB = "DUB";
    private static final String AIRPORT_WRO = "WRO";
    private static final String AIRPORT_LUZ = "LUZ";
    public static final AirportIataCode AIRPORT_CODE_LUZ = new AirportIataCode(AIRPORT_LUZ);
    private static final String AIRPORT_CHQ = "CHQ";
    public static final AirportIataCode AIRPORT_CODE_CHQ = new AirportIataCode(AIRPORT_CHQ);
    private static final String AIRPORT_SKG = "SKG";
    public static final AirportIataCode AIRPORT_CODE_SKG = new AirportIataCode(AIRPORT_SKG);

    private static final GetInterconnectedFlightUseCase.Query VALID_QUERY = GetInterconnectedFlightUseCase.Query.builder().departure(new AirportIataCode(AIRPORT_DUB)).arrival(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(LOCAL_DATE_TIME)).arrivalDateTime(new FlightDateTime(LOCAL_DATE_TIME.plusDays(1))).build();

    @Test
    void emptyGraph_returnEmptyFlux() {
        final var adapter =new CalculatePathAdapter();
        Flux<InterconnectedFlight> interconnectedFlightFlux = adapter.calculateInterconnectedFlights(VALID_QUERY, new DirectedWeightedMultigraph<>(FlightScheduleWeightedEdge.class));
        StepVerifier.create(interconnectedFlightFlux).expectNextCount(0).verifyComplete();

    }

    @Test
    void graphWithNoVertexInTheGraph_returnEmptyFlux() {
        final var adapter =new CalculatePathAdapter();
        final var routesGraph = new DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge>(FlightScheduleWeightedEdge.class);
        Graphs.addAllVertices(routesGraph, List.of(AIRPORT_CODE_LUZ, AIRPORT_CODE_CHQ, AIRPORT_CODE_SKG) );
        routesGraph.addEdge(AIRPORT_CODE_LUZ, AIRPORT_CODE_CHQ,  new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,11,0), LocalDateTime.of(2021,11,1,12,0)));
        routesGraph.addEdge(AIRPORT_CODE_CHQ, AIRPORT_CODE_SKG,  new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,11,0), LocalDateTime.of(2021,11,1,12,0)));

        assertThrows(IllegalArgumentException.class, () -> adapter.calculateInterconnectedFlights(GetInterconnectedFlightUseCase.Query.builder().departure(new AirportIataCode("AAA")).arrival(new AirportIataCode("BBB"))
                .departureDateTime(new FlightDateTime(LOCAL_DATE_TIME)).arrivalDateTime(new FlightDateTime(LOCAL_DATE_TIME.plusDays(1))).build(), routesGraph));
    }


}