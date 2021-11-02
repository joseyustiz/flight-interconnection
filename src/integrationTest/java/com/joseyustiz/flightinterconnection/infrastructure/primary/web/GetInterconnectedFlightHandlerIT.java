package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightDelegate;
import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.infrastructure.config.ApplicationConfig;
import com.joseyustiz.flightinterconnection.infrastructure.config.WebRouterFunctionConfig;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.InterconnectedFlightDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

import static com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import({WebRouterFunctionConfig.class, ApplicationConfig.class, GetInterconnectedFlightHandler.class})
public class GetInterconnectedFlightHandlerIT {
    private static final String AIRPORT_DUB = "DUB";
    private static final String AIRPORT_WRO = "WRO";
    private static final String AIRPORT_STN = "STN";
    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.parse("2018-03-01T12:40");
    private static final LocalDateTime DEPARTURE_DATE_TIME2 = LocalDateTime.parse("2018-03-01T06:25");
    private static final LocalDateTime DEPARTURE_DATE_TIME3 = LocalDateTime.parse("2018-03-01T09:50");
    private static final LocalDateTime ARRIVAL_DATE_TIME = LocalDateTime.parse("2018-03-01T16:40");
    private static final LocalDateTime ARRIVAL_DATE_TIME2 = LocalDateTime.parse("2018-03-01T07:35");
    private static final LocalDateTime ARRIVAL_DATE_TIME3 = LocalDateTime.parse("2018-03-01T13:20");
    private static final FlightSchedule FLIGHT_SCHEDULE = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_DUB))
            .arrivalAirport(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME))
            .build();

    private static final FlightSchedule FLIGHT_SCHEDULE2 = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_DUB))
            .arrivalAirport(new AirportIataCode(AIRPORT_STN))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME2))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME2))
            .build();

    private static final FlightSchedule FLIGHT_SCHEDULE3 = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_STN))
            .arrivalAirport(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME3))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME3))
            .build();
    private static final InterconnectedFlight INTERCONNECTED_FLIGHT = InterconnectedFlight.builder().stops(0).legs(List.of(FLIGHT_SCHEDULE)).build();
    private static final InterconnectedFlight INTERCONNECTED_FLIGHT2 = InterconnectedFlight.builder().stops(1).legs(List.of(FLIGHT_SCHEDULE2, FLIGHT_SCHEDULE3)).build();

    private static final InterconnectedFlightDto INTERCONNECTED_FLIGHT_DTO = InterconnectedFlightMapper.INSTANCE.toInterconnectedFlightDto(INTERCONNECTED_FLIGHT);
    private static final InterconnectedFlightDto INTERCONNECTED_FLIGHT_DTO2 = InterconnectedFlightMapper.INSTANCE.toInterconnectedFlightDto(INTERCONNECTED_FLIGHT2);

    @Autowired
    private WebTestClient webClient;
    @MockBean
    private GetInterconnectedFlightDelegate delegate;

    @MockBean
    private GetInterconnectedFlightUseCase service;

    @MockBean
    private InterconnectedFlightMapper flightMapper;

    @Test
    void missingRequiredParameters_returnBadRequest() {
        //given:
        final var url = "/v1/flights/interconnections";
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[*].property")
                .value(hasItems(DEPARTURE_QUERY_PARAM, DEPARTURE_DATE_TIME_QUERY_PARAM,
                        ARRIVAL_QUERY_PARAM, ARRIVAL_DATE_TIME_QUERY_PARAM))
                .jsonPath("$.errors.size()").isEqualTo(4)
                .jsonPath("$.errors[*].message").value(hasItems(MUST_NOT_BE_NULL));


    }

    @Test
    void AnExceptionInTheService_returnInternalServerError() {
        //given:
        final var url = "/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00";
        when(service.handle(ArgumentMatchers.any())).thenThrow(new RuntimeException("Exception"));
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is5xxServerError();

    }

    @Test
    void AnExceptionInTheMapper_returnInternalServerError() {
        //given:
        final var url = "/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00";
        when(service.handle(any())).thenReturn(Flux.just(InterconnectedFlight.builder().stops(1).build()));
        when(flightMapper.toInterconnectedFlightDto(any())).thenThrow(new RuntimeException("Exception"));
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is5xxServerError();

    }

    @Test
    void validValueFromTheService_returnProperContent() {
        //given:
        final var url = "/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00";
        when(service.handle(any())).thenReturn(Flux.fromIterable(List.of(INTERCONNECTED_FLIGHT, INTERCONNECTED_FLIGHT2)));

        when(flightMapper.toInterconnectedFlightDto(eq(INTERCONNECTED_FLIGHT))).thenReturn(INTERCONNECTED_FLIGHT_DTO);
        when(flightMapper.toInterconnectedFlightDto(eq(INTERCONNECTED_FLIGHT2))).thenReturn(INTERCONNECTED_FLIGHT_DTO2);
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$", hasItems(INTERCONNECTED_FLIGHT_DTO, INTERCONNECTED_FLIGHT_DTO2));


    }

    @Test
    void emptyInterconnectedFlight_returnEmptyBody() {
        //given:
        final var url = "/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00";
        when(service.handle(any())).thenReturn(Flux.empty());
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.size()").isEqualTo(0);
    }

}
