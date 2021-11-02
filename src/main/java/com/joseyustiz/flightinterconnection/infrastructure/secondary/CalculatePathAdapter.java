package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.dto.FlightScheduleWeightedEdge;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CalculatePathAdapter implements CalculatePathPort {
    private static final int MIN_CONNECTION_TIME = 2;

    public Flux<InterconnectedFlight> calculateInterconnectedFlights(@NonNull GetInterconnectedFlightUseCase.Query query,
                                                                     @NonNull DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge> routesGraph) {


        if (routesGraph.edgeSet().size() == 0) {
            return Flux.empty();
        }
        final var resultPaths = new ArrayList<GraphPath<AirportIataCode, FlightScheduleWeightedEdge>>();

        final var allDirectedPaths = new AllDirectedPaths<>(routesGraph);
        final var allPaths = allDirectedPaths.getAllPaths(query.getDeparture(), query.getArrival(), true, 100);
        allPaths.forEach(path -> {
                    final var connections = path.getEdgeList();
                    if (isDirectFlight(path)) {
                        final var flightScheduleWeightedEdge = connections.get(0);
                        if (isFlightWithInDesiredSchedule(query, flightScheduleWeightedEdge))
                            resultPaths.add(path);
                    } else {
                        var isInvalid = false;
                        for (int i = 0; i < connections.size() - 1; i++) {
                            final var actualFlightScheduleWeightedEdge = connections.get(i);
                            final var nextFlightScheduleWeightedEdge = connections.get(i + 1);

                            if (isInvalidRoute(query, actualFlightScheduleWeightedEdge, nextFlightScheduleWeightedEdge)) {
                                isInvalid = true;
                                break;
                            }
                        }
                        if (!isInvalid) {
                            resultPaths.add(path);
                        }
                    }
                }
        );
        log.info("resultPaths={}", resultPaths);
        return Flux.fromStream(resultPaths.stream()
                .map(this::mapToInterconnectedFlight));
    }

    public boolean isFlightWithInDesiredSchedule(GetInterconnectedFlightUseCase.Query query, FlightScheduleWeightedEdge flightScheduleWeightedEdge) {
        return !flightScheduleWeightedEdge.getDepartureDateTime().isBefore(query.getDepartureDateTime().getValue()) && !flightScheduleWeightedEdge.getArrivalDateTime().isAfter(query.getArrivalDateTime().getValue());
    }

    private boolean isDirectFlight(GraphPath<AirportIataCode, FlightScheduleWeightedEdge> path) {
        return path.getLength() == 1;
    }

    public boolean isInvalidRoute(GetInterconnectedFlightUseCase.Query query, FlightScheduleWeightedEdge actualFlightScheduleWeightedEdge, FlightScheduleWeightedEdge nextFlightScheduleWeightedEdge) {
        return isDepartureDateTimeBeforeDesired(query, actualFlightScheduleWeightedEdge)
                || isArrivalDateTimeAfterDesired(query, nextFlightScheduleWeightedEdge)
                || isInsufficientConnectionTime(actualFlightScheduleWeightedEdge, nextFlightScheduleWeightedEdge);
    }

    public boolean isInsufficientConnectionTime(FlightScheduleWeightedEdge actualFlightScheduleWeightedEdge, FlightScheduleWeightedEdge nextFlightScheduleWeightedEdge) {
        return ChronoUnit.HOURS.between(actualFlightScheduleWeightedEdge.getArrivalDateTime(), nextFlightScheduleWeightedEdge.getDepartureDateTime()) < MIN_CONNECTION_TIME;
    }

    public boolean isArrivalDateTimeAfterDesired(GetInterconnectedFlightUseCase.Query query, FlightScheduleWeightedEdge nextFlightScheduleWeightedEdge) {
        return nextFlightScheduleWeightedEdge.getArrivalAirport().equals(query.getArrival()) && nextFlightScheduleWeightedEdge.getArrivalDateTime().isAfter(query.getArrivalDateTime().getValue());
    }

    public boolean isDepartureDateTimeBeforeDesired(GetInterconnectedFlightUseCase.Query query, FlightScheduleWeightedEdge actualFlightScheduleWeightedEdge) {
        return actualFlightScheduleWeightedEdge.getDepartureAirport().equals(query.getDeparture()) && actualFlightScheduleWeightedEdge.getDepartureDateTime().isBefore(query.getDepartureDateTime().getValue());
    }

    public InterconnectedFlight mapToInterconnectedFlight(GraphPath<AirportIataCode, FlightScheduleWeightedEdge> path) {
        return InterconnectedFlight.builder()
                .stops(path.getLength())
                .legs(path.getEdgeList().stream()
                        .map(this::mapToFlightSchedule)
                        .collect(Collectors.toList()))
                .build();
    }

    private FlightSchedule mapToFlightSchedule(FlightScheduleWeightedEdge edge) {
        return FlightSchedule.builder()
                .departureAirport(edge.getDepartureAirport())
                .departureDateTime(new FlightDateTime(edge.getDepartureDateTime()))
                .arrivalAirport(edge.getArrivalAirport())
                .arrivalDateTime(new FlightDateTime(edge.getArrivalDateTime()))
                .build();
    }
}
