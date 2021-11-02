package com.joseyustiz.flightinterconnection.core.service;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightService;
import com.joseyustiz.flightinterconnection.core.domain.*;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.dto.FlightScheduleWeightedEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase.Query;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GetInterconnectedFlightServiceTest {
    private static final String AIRPORT_STN = "STN";
    private static final String AIRPORT_DUB = "DUB";
    private static final String AIRPORT_WRO = "WRO";
    private static final String AIRPORT_LUZ = "LUZ";
    private static final String AIRPORT_CHQ = "CHQ";
    private static final String AIRPORT_SKG = "SKG";
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2018, 3, 1, 5, 0);
    private static final Query VALID_QUERY = Query.builder().departure(new AirportIataCode(AIRPORT_DUB)).arrival(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(LOCAL_DATE_TIME)).arrivalDateTime(new FlightDateTime(LOCAL_DATE_TIME.plusDays(1))).build();

    private static final Route ROUTE_DUB_WRO = Route.builder().arrivalAirport(new AirportIataCode(AIRPORT_DUB))
            .departureAirport(new AirportIataCode(AIRPORT_WRO))
            .build();
    private static final Route ROUTE_DUB_STN = Route.builder().arrivalAirport(new AirportIataCode(AIRPORT_DUB))
            .departureAirport(new AirportIataCode(AIRPORT_STN))
            .build();

    private static final Route ROUTE_STN_WRO = Route.builder().arrivalAirport(new AirportIataCode(AIRPORT_STN))
            .departureAirport(new AirportIataCode(AIRPORT_WRO))
            .build();


    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.parse("2018-03-01T12:40");
    private static final LocalDateTime DEPARTURE_DATE_TIME2 = LocalDateTime.parse("2018-03-01T06:25");
    private static final LocalDateTime DEPARTURE_DATE_TIME3 = LocalDateTime.parse("2018-03-01T09:50");
    private static final LocalDateTime ARRIVAL_DATE_TIME = LocalDateTime.parse("2018-03-01T16:40");
    private static final LocalDateTime ARRIVAL_DATE_TIME2 = LocalDateTime.parse("2018-03-01T07:35");
    private static final LocalDateTime ARRIVAL_DATE_TIME3 = LocalDateTime.parse("2018-03-01T13:20");
    private static final FlightSchedule FLIGHT_SCHEDULE_DUB_WRO = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_DUB))
            .arrivalAirport(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME))
            .build();

    private static final FlightSchedule FLIGHT_SCHEDULE_DUB_STN = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_DUB))
            .arrivalAirport(new AirportIataCode(AIRPORT_STN))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME2))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME2))
            .build();

    private static final FlightSchedule FLIGHT_SCHEDULE_STN_WRO = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_STN))
            .arrivalAirport(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME3))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME3))
            .build();
    private static final InterconnectedFlight INTERCONNECTED_FLIGHT = InterconnectedFlight.builder().stops(0).legs(List.of(FLIGHT_SCHEDULE_DUB_WRO)).build();
    private static final InterconnectedFlight INTERCONNECTED_FLIGHT2 = InterconnectedFlight.builder().stops(1).legs(List.of(FLIGHT_SCHEDULE_DUB_STN, FLIGHT_SCHEDULE_STN_WRO)).build();

    private static RoutePort routePort;
    private static SchedulePort schedulePort;
    private static CalculatePathPort calculatePathPort;
    private static GetInterconnectedFlightUseCase service;

    @BeforeAll
    static void beforeAll() {
        routePort = Mockito.mock(RoutePort.class);
        schedulePort = Mockito.mock(SchedulePort.class);
        calculatePathPort = Mockito.mock(CalculatePathPort.class);
        service = new GetInterconnectedFlightService(routePort, schedulePort, calculatePathPort);
    }

    @Test
    void invalidQueryValues_throwsConstraintViolationException() {
        final var exception = assertThrows(ConstraintViolationException.class, () -> Query.builder().departure(new AirportIataCode("")).arrival(new AirportIataCode("")).departureDateTime(null).arrivalDateTime(null).build());
        assertThat(exception.getConstraintViolations().size()).isEqualTo(4);
    }

    @Test
    void routePortThrowsException_serviceRethrowIt() {
        final var message = "Exception during getting route";
        when(routePort.getRoutesByConnectingAirportAndOperatorAsList(any(AirportIataCode.class), any(Airline.class)))
                .thenThrow(new RuntimeException(message));
        final var exception = assertThrows(RuntimeException.class, () -> service.handle(VALID_QUERY));

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void schedulePortThrowsException_serviceRethrowIt() {
        final var message = "Exception during getting schedule";
        when(routePort.getRoutesByConnectingAirportAndOperatorAsList(any(AirportIataCode.class), any(Airline.class)))
                .thenReturn(Set.of(ROUTE_DUB_WRO, ROUTE_DUB_STN));
        when(schedulePort.getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(any(AirportIataCode.class), any(AirportIataCode.class), any(Query.class), any(ScheduleYearMonth.class)))
                .thenThrow(new RuntimeException(message));
        final var exception = assertThrows(RuntimeException.class, () -> service.handle(VALID_QUERY));

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void calculatePathPortThrowsException_serviceRethrowIt() {
        final var message = "Exception during calculating Interconnected Flights";
        when(routePort.getRoutesByConnectingAirportAndOperatorAsList(any(AirportIataCode.class), any(Airline.class)))
                .thenReturn(Set.of(ROUTE_DUB_WRO, ROUTE_DUB_STN));
        when(schedulePort.getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(any(AirportIataCode.class), any(AirportIataCode.class), any(Query.class), any(ScheduleYearMonth.class)))
                .thenReturn(Collections.emptyList());
        when(calculatePathPort.calculateInterconnectedFlights(any(), any()))
                .thenThrow(new RuntimeException(message));
        final var exception = assertThrows(RuntimeException.class, () -> service.handle(VALID_QUERY));

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void departureDateTimeEqualToArrivalDateTime_emptyInterconnectedFlights2() {
        when(routePort.getRoutesByConnectingAirportAndOperatorAsList(any(AirportIataCode.class), any(Airline.class)))
                .thenReturn(Set.of(ROUTE_DUB_WRO, ROUTE_DUB_STN));
        when(schedulePort.getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(any(AirportIataCode.class), any(AirportIataCode.class), any(Query.class), any(ScheduleYearMonth.class)))
                .thenReturn(Collections.emptyList());
        when(calculatePathPort.calculateInterconnectedFlights(any(), any())).thenReturn(Flux.just(INTERCONNECTED_FLIGHT, INTERCONNECTED_FLIGHT2));
        final var interconnectedFlightFlux = service.handle(VALID_QUERY);

        assertThat(interconnectedFlightFlux).isNotNull();
    }

    @Test
    void name() {
        final var airport0 = new AirportIataCode("0");
        final var airport1 = new AirportIataCode("1");
        final var desiredDeparture = new AirportIataCode("2");
        final var desiredArrival = new AirportIataCode("3");

//        Route.builder().departureAirport(airport0).arrivalAirport(desiredArrival).build();
//        Route.builder().departureAirport(airport0).arrivalAirport(desiredArrival).build();

        DirectedWeightedMultigraph<AirportIataCode, FlightScheduleWeightedEdge> graph =
                new DirectedWeightedMultigraph<>(FlightScheduleWeightedEdge.class);
        Graphs.addAllVertices(graph, Arrays.asList(airport0, airport1, desiredDeparture, desiredArrival));
        graph.addVertex(airport0);

        graph.addEdge(airport0, desiredArrival, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 11, 0), LocalDateTime.of(2021, 11, 1, 12, 0)));
        graph.addEdge(airport0, airport1, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 13, 0), LocalDateTime.of(2021, 11, 1, 15, 0)));
        graph.addEdge(airport0, desiredDeparture, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 14, 0), LocalDateTime.of(2021, 11, 1, 18, 0)));
        graph.addEdge(airport1, desiredArrival, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 16, 0), LocalDateTime.of(2021, 11, 1, 18, 0)));
        graph.addEdge(desiredDeparture, airport0, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 8, 0), LocalDateTime.of(2021, 11, 1, 10, 0)));
        graph.addEdge(desiredDeparture, airport0, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 6, 0), LocalDateTime.of(2021, 11, 1, 8, 0)));
        graph.addEdge(desiredDeparture, airport1, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 9, 0), LocalDateTime.of(2021, 11, 1, 13, 0)));
        graph.addEdge(desiredDeparture, desiredArrival, new FlightScheduleWeightedEdge(LocalDateTime.of(2021, 11, 1, 12, 0), LocalDateTime.of(2021, 11, 1, 15, 0)));

        Iterator<AirportIataCode> iterator = new DepthFirstIterator<>(graph, desiredDeparture);
        while (iterator.hasNext()) {
            AirportIataCode airportIataCode = iterator.next();
            System.out.println(airportIataCode);
        }
        System.out.println("-----------------------------");
        AllDirectedPaths<AirportIataCode, FlightScheduleWeightedEdge> allDirectedPaths = new AllDirectedPaths<>(graph);

        LocalDateTime desiredDepartureDatetime = LocalDateTime.of(2021, 11, 1, 8, 0);
        LocalDateTime desiredArrivalDatetime = LocalDateTime.of(2021, 11, 1, 20, 0);

        List<GraphPath<AirportIataCode, FlightScheduleWeightedEdge>> resultPaths = new ArrayList<>();

        final List<GraphPath<AirportIataCode, FlightScheduleWeightedEdge>> allPaths = allDirectedPaths.getAllPaths(desiredDeparture, desiredArrival, true, 100);
        for (GraphPath<AirportIataCode, FlightScheduleWeightedEdge> path : allPaths) {
            final var connections = path.getEdgeList();
            if (path.getLength() == 1) {
                final var flightScheduleWeightedEdge = connections.get(0);
                if (!flightScheduleWeightedEdge.getDepartureDateTime().isBefore(desiredDepartureDatetime) && !flightScheduleWeightedEdge.getArrivalDateTime().isAfter(desiredArrivalDatetime))
                    resultPaths.add(path);
            } else {
                boolean invalid = false;
                //i=0 -> i< 2
                for (int i = 0; i < connections.size() - 1; i++) {
                    final var actualFlightScheduleWeightedEdge = connections.get(i);
                    final var nextFlightScheduleWeightedEdge = connections.get(i + 1);

                    if ((actualFlightScheduleWeightedEdge.getDepartureAirport().equals(desiredDeparture) && actualFlightScheduleWeightedEdge.getDepartureDateTime().isBefore(desiredDepartureDatetime))
                            || (nextFlightScheduleWeightedEdge.getArrivalAirport().equals(desiredArrival) && nextFlightScheduleWeightedEdge.getArrivalDateTime().isAfter(desiredArrivalDatetime))
                            || (ChronoUnit.HOURS.between(actualFlightScheduleWeightedEdge.getArrivalDateTime(), nextFlightScheduleWeightedEdge.getDepartureDateTime()) < 2)) {
                        invalid = true;
                        break;
                    }

                }
                if (!invalid) {
                    resultPaths.add(path);
                }
            }
        }

        resultPaths.forEach(System.out::println);
    }
}