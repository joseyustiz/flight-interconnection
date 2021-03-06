package com.joseyustiz.flightinterconnection.core;

import com.joseyustiz.flightinterconnection.core.domain.*;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.dto.FlightScheduleWeightedEdge;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase.Query;

@Slf4j
@RequiredArgsConstructor
public class GetInterconnectedFlightDelegate {
    private static final Airline RYANAIR = new Airline("RYANAIR");
    private final RoutePort routePort;
    private final SchedulePort schedulePort;
    private final CalculatePathPort calculatePathPort;

    @Cacheable("interconnectedFlight")
    public List<InterconnectedFlight> getInterconnectedFlight(Query query) {
        final var searchYearMonthRangeCollection = query.getDepartureDateTime().getListOfDatesUntil(query.getDepartureDateTime());
        log.info("The search YearMonth range is of size={}", searchYearMonthRangeCollection.size());
        final var ryanairRoutesCollection = routePort.getRoutesByConnectingAirportAndOperatorAsList(AirportIataCode.NULL_AIRPORT, RYANAIR);
        log.info("The total ryanair routes are={}",ryanairRoutesCollection.size());
        final var routesGraph = calculateRoutesGraph(searchYearMonthRangeCollection, query, ryanairRoutesCollection);
        log.info("***********The routes graph has number of edges={}***************",routesGraph== null || routesGraph.edgeSet() == null? null: routesGraph.edgeSet().size());
        return calculatePathPort.calculateInterconnectedFlightsCollection(query, routesGraph);
    }

    private DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge>
    calculateRoutesGraph(@NonNull List<ScheduleYearMonth> yearMonthRangeDesired,@NonNull Query query,@NonNull Set<Route> routes) {
        final var routesGraph = new DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge>(FlightScheduleWeightedEdge.class);
        if(!routes.isEmpty()) {
            routes.parallelStream().forEach(r -> {
                routesGraph.addVertex(r.getDepartureAirport());
                routesGraph.addVertex(r.getArrivalAirport());
                yearMonthRangeDesired.stream().filter(Objects::nonNull).forEach(yearMonth -> {
                            schedulePort.getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(
                                            r.getDepartureAirport(), r.getArrivalAirport(), query, yearMonth)
                                    .stream().filter(Objects::nonNull).collect(Collectors.toList())
                                    .forEach(fs -> {
                                                log.info("adding Edge = {}", fs);
                                                routesGraph.addEdge(r.getDepartureAirport(), r.getArrivalAirport(),
                                                        new FlightScheduleWeightedEdge(fs.getDepartureDateTime().getValue(), fs.getArrivalDateTime().getValue())
                                                );
                                            }
                                    );
                        }
                );
            });
        }
        return routesGraph;
    }

}
