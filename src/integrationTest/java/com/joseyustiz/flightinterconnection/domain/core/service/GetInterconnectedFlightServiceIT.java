package com.joseyustiz.flightinterconnection.domain.core.service;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightDelegate;
import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightService;
import com.joseyustiz.flightinterconnection.core.domain.*;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.CalculatePathAdapter;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.dto.FlightScheduleWeightedEdge;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class GetInterconnectedFlightServiceIT {
    RoutePort routePort = Mockito.mock(RoutePort.class);
    SchedulePort schedulePort = Mockito.mock(SchedulePort.class);
    CalculatePathPort calculatePathPort = new CalculatePathAdapter();
    GetInterconnectedFlightDelegate delegate = new GetInterconnectedFlightDelegate(routePort, schedulePort, calculatePathPort);
    GetInterconnectedFlightService service = new GetInterconnectedFlightService(delegate);


    @Test
    void validGraph_returnOnlyRoutesWithInScheduleAndConnectionsHave2OrMoreHours() {
        final var airportAAA = new AirportIataCode("AAA");
        final var airportBBB = new AirportIataCode("BBB");
        final var desiredDeparture = new AirportIataCode("CCC");
        final var desiredArrival = new AirportIataCode("DDD");

        DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge> graph =
                new DirectedWeightedMultigraph<>(FlightScheduleWeightedEdge.class);
        Graphs.addAllVertices(graph, Arrays.asList(airportAAA, airportBBB, desiredDeparture, desiredArrival));
        graph.addVertex(airportAAA);

        graph.addEdge(airportAAA, desiredArrival, new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,11,0), LocalDateTime.of(2021,11,1,12,0)));
        graph.addEdge(airportAAA, airportBBB, new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,13,0), LocalDateTime.of(2021,11,1,15,0)));
        graph.addEdge(airportAAA, desiredDeparture, new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,14,0), LocalDateTime.of(2021,11,1,18,0)));
        graph.addEdge(airportBBB, desiredArrival, new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,16,0), LocalDateTime.of(2021,11,1,18,0)));
        graph.addEdge(desiredDeparture, airportAAA, new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,8,0), LocalDateTime.of(2021,11,1,10,0)));
        graph.addEdge(desiredDeparture, airportAAA, new FlightScheduleWeightedEdge(LocalDateTime.of(2021,11,1,6,0), LocalDateTime.of(2021,11,1,8,0)));
        graph.addEdge(desiredDeparture, airportBBB, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 9, 0), LocalDateTime.of(2021, 11, 1, 13, 0)));
        graph.addEdge(desiredDeparture, desiredArrival, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 12, 0), LocalDateTime.of(2021, 11, 1, 15, 0)));

        LocalDateTime desiredDepartureDatetime = LocalDateTime.of(2021, 11, 1, 8, 0);
        LocalDateTime desiredArrivalDatetime = LocalDateTime.of(2021, 11, 1, 20, 0);


        var query = GetInterconnectedFlightUseCase.Query.builder()
                .departure(desiredDeparture)
                .arrival(desiredArrival)
                .arrivalDateTime(new FlightDateTime(desiredArrivalDatetime))
                .departureDateTime(new FlightDateTime(desiredDepartureDatetime))
                .build();

        var routes = new HashSet<Route>();
        routes.add(Route.builder().departureAirport(airportAAA).arrivalAirport(desiredArrival).build());
        routes.add(Route.builder().departureAirport(airportAAA).arrivalAirport(airportBBB).build());
        routes.add(Route.builder().departureAirport(airportAAA).arrivalAirport(desiredDeparture).build());
        routes.add(Route.builder().departureAirport(airportBBB).arrivalAirport(desiredArrival).build());
        routes.add(Route.builder().departureAirport(desiredDeparture).arrivalAirport(airportAAA).build());
        routes.add(Route.builder().departureAirport(desiredDeparture).arrivalAirport(airportBBB).build());
        routes.add(Route.builder().departureAirport(desiredDeparture).arrivalAirport(desiredArrival).build());

        var flightSchedules = new ArrayList<FlightSchedule>();
        when(routePort.getRoutesByConnectingAirportAndOperatorAsList(any(), any())).thenReturn(routes);


        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(airportAAA).arrivalAirport(desiredArrival)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 11, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 12, 0)))
                .build());

        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(airportAAA).arrivalAirport(airportBBB)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 13, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 15, 0)))
                .build());

        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(airportAAA).arrivalAirport(desiredDeparture)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 14, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 18, 0)))
                .build());

        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(airportBBB).arrivalAirport(desiredArrival)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 16, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 18, 0)))
                .build());
        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(desiredDeparture).arrivalAirport(airportAAA)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 8, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 10, 0)))
                .build());
        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(desiredDeparture).arrivalAirport(airportAAA)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 6, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 8, 0)))
                .build());
        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(desiredDeparture).arrivalAirport(airportBBB)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 9, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 13, 0)))
                .build());
        flightSchedules.add(FlightSchedule.builder()
                .departureAirport(desiredDeparture).arrivalAirport(desiredArrival)
                .departureDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 12, 0)))
                .arrivalDateTime(new FlightDateTime(LocalDateTime.of(2021, 11, 1, 15, 0)))
                .build());
        for (FlightSchedule f: flightSchedules) {
            when(schedulePort.getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(eq(f.getDepartureAirport()),
                    eq(f.getArrivalAirport()), eq(query), eq(new ScheduleYearMonth(YearMonth.of(2021,11))))).thenReturn(List.of(f));
        }

        final var interconnectedFlights = service.handle(query);

        StepVerifier.create(interconnectedFlights)

                .expectNextMatches(interconnectedFlight-> interconnectedFlight.getStops()==1)
                .expectNextMatches(interconnectedFlight->interconnectedFlight.getStops()==2)
                .verifyComplete();

    }
}
