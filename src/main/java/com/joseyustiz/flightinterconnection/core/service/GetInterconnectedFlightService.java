package com.joseyustiz.flightinterconnection.core.service;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.domain.*;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.dto.FlightScheduleWeightedEdge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class GetInterconnectedFlightService implements GetInterconnectedFlightUseCase {
    private static final Airline RYANAIR = new Airline("RYANAIR");
    private final RoutePort routePort;
    private final SchedulePort schedulePort;
    private final CalculatePathPort calculatePathPort;

    @Override
    public Flux<InterconnectedFlight> handle(Query query) {
        final var searchYearMonthRangeCollection = query.getDepartureDateTime().getListOfDatesUntil(query.getDepartureDateTime());
        final var ryanairRoutesCollection = routePort.getRoutesByConnectingAirportAndOperatorAsList(AirportIataCode.NULL_AIRPORT, RYANAIR);
        final var routesGraph = calculateRoutesGraph(searchYearMonthRangeCollection, ryanairRoutesCollection);
        return calculatePathPort.calculateInterconnectedFlights(query, routesGraph);

    }

    public DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge>
    calculateRoutesGraph(List<ScheduleYearMonth> yearMonthRangeDesired, Set<Route> routes) {
        final var routesGraph = new DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge>(FlightScheduleWeightedEdge.class);
        routes.forEach(r -> {
            routesGraph.addVertex(r.getDepartureAirport());
            routesGraph.addVertex(r.getArrivalAirport());
            yearMonthRangeDesired.forEach(yearMonth ->
                    schedulePort.getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(
                                    r.getDepartureAirport(), r.getArrivalAirport(), yearMonth)
                            .forEach(fs -> routesGraph.addEdge(r.getDepartureAirport(), r.getArrivalAirport(),
                                            new FlightScheduleWeightedEdge(fs.getDepartureDateTime().getValue(), fs.getArrivalDateTime().getValue())
                                    )
                            )
            );
        });
        return routesGraph;
    }

}
